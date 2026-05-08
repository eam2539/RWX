package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.net.NetworkInterface
import java.net.URI
import java.net.URL
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory

class NatPortMapper {
    data class Mapping(
        val externalIp: String,
        val externalPort: Int,
        val internalPort: Int,
        val protocol: String,
        val method: String,
        val controlUrl: URL? = null,
        val serviceType: String? = null,
        val pcpServer: InetAddress? = null,
        val pcpNonce: ByteArray? = null
    )

    private data class UpnpService(
        val controlUrl: URL,
        val serviceType: String
    )

    fun mapTcpPort(
        internalPort: Int,
        description: String,
        enablePcp: Boolean = true,
        enableUpnp: Boolean = true,
        pcpLifetimeSeconds: Int = 3600,
        pcpTimeoutMs: Int = 3000
    ): Mapping? {
        if (enablePcp) {
            val pcpMapping = runCatching { mapTcpPortWithPcp(internalPort, pcpLifetimeSeconds, pcpTimeoutMs) }
                .onFailure { GameEngine.log("PCP port mapping failed: ${it.message}") }
                .getOrNull()
            if (pcpMapping != null) return pcpMapping
        }
        if (!enableUpnp) return null

        val service = discoverService() ?: return null
        val localAddress = getLocalAddressFor(service.controlUrl.host) ?: return null
        val externalIp = getExternalIp(service) ?: return null
        val mapped = addPortMapping(
            service = service,
            externalPort = internalPort,
            internalPort = internalPort,
            internalClient = localAddress.hostAddress,
            protocol = "TCP",
            description = description
        )
        if (!mapped) return null

        return Mapping(
            externalIp = externalIp,
            externalPort = internalPort,
            internalPort = internalPort,
            protocol = "TCP",
            method = "UPnP",
            controlUrl = service.controlUrl,
            serviceType = service.serviceType
        )
    }

    fun deleteMapping(mapping: Mapping) {
        if (mapping.method == "PCP") {
            runCatching { deletePcpMapping(mapping) }
                .onFailure { GameEngine.log("PCP delete port mapping failed: ${it.message}") }
            return
        }
        val controlUrl = mapping.controlUrl ?: return
        val serviceType = mapping.serviceType ?: return
        runCatching {
            soapRequest(
                controlUrl = controlUrl,
                serviceType = serviceType,
                action = "DeletePortMapping",
                body = """
                    <NewRemoteHost></NewRemoteHost>
                    <NewExternalPort>${mapping.externalPort}</NewExternalPort>
                    <NewProtocol>${mapping.protocol}</NewProtocol>
                """.trimIndent()
            )
        }.onFailure { e ->
            GameEngine.log("UPnP delete port mapping failed: ${e.message}")
        }
    }

    private fun mapTcpPortWithPcp(internalPort: Int, lifetimeSeconds: Int, timeoutMs: Int): Mapping? {
        val gateway = getDefaultGatewayAddress() ?: return null
        val localAddress = getLocalAddressFor(gateway.hostAddress) ?: return null
        val nonce = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val request = buildPcpMapRequest(
            lifetimeSeconds = lifetimeSeconds,
            localAddress = localAddress,
            nonce = nonce,
            internalPort = internalPort,
            externalPort = internalPort
        )
        val response = sendPcpRequest(gateway, request, timeoutMs) ?: return null
        if (response.size < 60 || response[0].toInt() != 0x02 || response[1].toInt() != 0x81) return null
        val resultCode = response[3].toInt() and 0xff
        if (resultCode != 0) throw IOException("PCP MAP failed with result code $resultCode")
        val externalIpBytes = response.copyOfRange(44, 60)
        val externalIp = if (externalIpBytes.take(10)
                .all { it == 0.toByte() } && externalIpBytes[10] == 0xff.toByte() && externalIpBytes[11] == 0xff.toByte()
        ) {
            InetAddress.getByAddress(externalIpBytes.copyOfRange(12, 16)).hostAddress
        } else {
            InetAddress.getByAddress(externalIpBytes).hostAddress
        }
        val assignedExternalPort = ByteBuffer.wrap(response, 42, 2).short.toInt() and 0xffff
        return Mapping(
            externalIp = externalIp,
            externalPort = assignedExternalPort,
            internalPort = internalPort,
            protocol = "TCP",
            method = "PCP",
            pcpServer = gateway,
            pcpNonce = nonce
        )
    }

    private fun deletePcpMapping(mapping: Mapping) {
        val gateway = mapping.pcpServer ?: return
        val nonce = mapping.pcpNonce ?: return
        val localAddress = getLocalAddressFor(gateway.hostAddress) ?: return
        val request = buildPcpMapRequest(
            lifetimeSeconds = 0,
            localAddress = localAddress,
            nonce = nonce,
            internalPort = mapping.internalPort,
            externalPort = mapping.externalPort
        )
        sendPcpRequest(gateway, request, 2000)
    }

    private fun buildPcpMapRequest(
        lifetimeSeconds: Int,
        localAddress: InetAddress,
        nonce: ByteArray,
        internalPort: Int,
        externalPort: Int
    ): ByteArray {
        val buffer = ByteBuffer.allocate(60)
        buffer.put(2) // version
        buffer.put(1) // MAP opcode
        buffer.putShort(0)
        buffer.putInt(lifetimeSeconds.coerceAtLeast(0))
        buffer.put(toIpv6MappedAddress(localAddress))
        buffer.put(nonce)
        buffer.put(6) // TCP protocol
        buffer.put(ByteArray(3))
        buffer.putShort(internalPort.toShort())
        buffer.putShort(externalPort.toShort())
        buffer.put(ByteArray(16)) // suggested external address: any
        return buffer.array()
    }

    private fun sendPcpRequest(server: InetAddress, request: ByteArray, timeoutMs: Int): ByteArray? {
        val socket = DatagramSocket()
        return try {
            socket.soTimeout = timeoutMs
            val packet = DatagramPacket(request, request.size, server, 5351)
            socket.send(packet)
            val responseBuffer = ByteArray(128)
            val response = DatagramPacket(responseBuffer, responseBuffer.size)
            socket.receive(response)
            response.data.copyOf(response.length)
        } finally {
            socket.close()
        }
    }

    private fun toIpv6MappedAddress(address: InetAddress): ByteArray {
        val bytes = address.address
        if (bytes.size == 16) return bytes
        return ByteArray(16).also {
            it[10] = 0xff.toByte()
            it[11] = 0xff.toByte()
            System.arraycopy(bytes, 0, it, 12, 4)
        }
    }

    private fun getDefaultGatewayAddress(): InetAddress? {
        return when {
            isWindows() -> getWindowsDefaultGatewayAddress()
            isMacOs() -> getMacOsDefaultGatewayAddress()
            else -> getLinuxDefaultGatewayAddress() ?: getIpRouteDefaultGatewayAddress()
        }
    }

    private fun getLinuxDefaultGatewayAddress(): InetAddress? {
        val routeFile = File("/proc/net/route")
        if (!routeFile.exists()) return null
        for (line in routeFile.readLines()) {
            val parts = line.trim().split(Regex("\\s+"))
            if (parts.size > 2 && parts[1] == "00000000") {
                val gatewayHex = parts[2]
                val bytes = ByteArray(4) { i -> gatewayHex.substring(i * 2, i * 2 + 2).toInt(16).toByte() }
                return InetAddress.getByAddress(byteArrayOf(bytes[3], bytes[2], bytes[1], bytes[0]))
            }
        }
        return null
    }

    private fun getIpRouteDefaultGatewayAddress(): InetAddress? {
        val output = runCommand(listOf("ip", "route", "show", "default")) ?: return null
        val match = Regex("\\bvia\\s+(\\d{1,3}(?:\\.\\d{1,3}){3})\\b").find(output) ?: return null
        return inetAddressOrNull(match.groupValues[1])
    }

    private fun getMacOsDefaultGatewayAddress(): InetAddress? {
        val output = runCommand(listOf("route", "-n", "get", "default")) ?: return null
        val match = Regex("(?m)^\\s*gateway:\\s*(\\d{1,3}(?:\\.\\d{1,3}){3})\\s*$").find(output) ?: return null
        return inetAddressOrNull(match.groupValues[1])
    }

    private fun getWindowsDefaultGatewayAddress(): InetAddress? {
        val output = runCommand(listOf("route", "print", "-4")) ?: return null
        val defaultRoute = output.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.startsWith("0.0.0.0") }
            ?: return null
        val parts = defaultRoute.split(Regex("\\s+"))
        if (parts.size < 3) return null
        return inetAddressOrNull(parts[2])
    }

    private fun runCommand(command: List<String>): String? {
        return runCatching {
            val process = ProcessBuilder(command).redirectErrorStream(true).start()
            val latch = CountDownLatch(1)
            Thread({
                runCatching { process.waitFor() }
                latch.countDown()
            }, "PCPRouteCommandWaiter").also { it.isDaemon = true }.start()
            val finished = latch.await(3, TimeUnit.SECONDS)
            if (!finished) {
                process.destroy()
                return@runCatching null
            }
            if (process.exitValue() != 0) return@runCatching null
            process.inputStream.use { String(it.readBytes(), Charsets.UTF_8) }
        }.getOrNull()
    }

    private fun inetAddressOrNull(value: String): InetAddress? {
        return runCatching { InetAddress.getByName(value) }.getOrNull()
    }

    private fun isWindows(): Boolean {
        return System.getProperty("os.name").lowercase(Locale.ROOT).contains("win")
    }

    private fun isMacOs(): Boolean {
        val osName = System.getProperty("os.name").lowercase(Locale.ROOT)
        return osName.contains("mac") || osName.contains("darwin")
    }

    private fun discoverService(): UpnpService? {
        val searchTargets = listOf(
            "urn:schemas-upnp-org:service:WANIPConnection:1",
            "urn:schemas-upnp-org:service:WANPPPConnection:1",
            "urn:schemas-upnp-org:device:InternetGatewayDevice:1"
        )

        for (target in searchTargets) {
            val locations = discoverLocations(target)
            for (location in locations) {
                val service = runCatching { parseInternetGatewayService(location) }.getOrNull()
                if (service != null) return service
            }
        }
        return null
    }

    private fun discoverLocations(searchTarget: String): List<URL> {
        val request = listOf(
            "M-SEARCH * HTTP/1.1",
            "HOST: 239.255.255.250:1900",
            "MAN: \"ssdp:discover\"",
            "MX: 2",
            "ST: $searchTarget"
        ).joinToString("\r\n", postfix = "\r\n\r\n")
        val requestBytes = request.toByteArray(Charsets.UTF_8)
        val locations = linkedSetOf<URL>()

        val socket = MulticastSocket()
        try {
            socket.soTimeout = 2500
            socket.timeToLive = 2
            val packet = DatagramPacket(
                requestBytes,
                requestBytes.size,
                InetAddress.getByName("239.255.255.250"),
                1900
            )

            val interfaces = NetworkInterface.getNetworkInterfaces().toList()
                .filter { it.isUp && !it.isLoopback && it.inetAddresses.toList().isNotEmpty() }

            if (interfaces.isEmpty()) {
                socket.send(packet)
            } else {
                interfaces.forEach { networkInterface ->
                    runCatching {
                        socket.networkInterface = networkInterface
                        socket.send(packet)
                    }
                }
            }

            val deadline = System.currentTimeMillis() + 3000L
            val buffer = ByteArray(8192)
            while (System.currentTimeMillis() < deadline) {
                val response = DatagramPacket(buffer, buffer.size)
                try {
                    socket.receive(response)
                } catch (_: Exception) {
                    break
                }
                val text = String(response.data, response.offset, response.length, Charsets.UTF_8)
                val location = text.lineSequence()
                    .firstOrNull { it.startsWith("location:", ignoreCase = true) }
                    ?.substringAfter(':')
                    ?.trim()
                if (!location.isNullOrBlank()) {
                    runCatching { URL(location) }.getOrNull()?.let { locations += it }
                }
            }
        } finally {
            socket.close()
        }
        return locations.toList()
    }

    private fun parseInternetGatewayService(location: URL): UpnpService? {
        val xml = httpGet(location)
        val document = documentBuilder().parse(ByteArrayInputStream(xml))
        val baseUrl = runCatching {
            document.getElementsByTagName("URLBase").item(0)?.textContent?.trim()?.let { URL(it) }
        }.getOrNull() ?: location

        val services = document.getElementsByTagName("service")
        for (i in 0 until services.length) {
            val service = services.item(i) as? Element ?: continue
            val serviceType = service.childText("serviceType") ?: continue
            if (serviceType != "urn:schemas-upnp-org:service:WANIPConnection:1" &&
                serviceType != "urn:schemas-upnp-org:service:WANPPPConnection:1"
            ) {
                continue
            }
            val controlUrlText = service.childText("controlURL") ?: continue
            return UpnpService(resolveUrl(baseUrl, controlUrlText), serviceType)
        }
        return null
    }

    private fun getExternalIp(service: UpnpService): String? {
        val response = soapRequest(
            controlUrl = service.controlUrl,
            serviceType = service.serviceType,
            action = "GetExternalIPAddress",
            body = ""
        )
        val document = documentBuilder().parse(ByteArrayInputStream(response))
        return document.getElementsByTagName("NewExternalIPAddress").item(0)?.textContent?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    private fun addPortMapping(
        service: UpnpService,
        externalPort: Int,
        internalPort: Int,
        internalClient: String,
        protocol: String,
        description: String
    ): Boolean {
        return runCatching {
            soapRequest(
                controlUrl = service.controlUrl,
                serviceType = service.serviceType,
                action = "AddPortMapping",
                body = """
                    <NewRemoteHost></NewRemoteHost>
                    <NewExternalPort>$externalPort</NewExternalPort>
                    <NewProtocol>$protocol</NewProtocol>
                    <NewInternalPort>$internalPort</NewInternalPort>
                    <NewInternalClient>$internalClient</NewInternalClient>
                    <NewEnabled>1</NewEnabled>
                    <NewPortMappingDescription>$description</NewPortMappingDescription>
                    <NewLeaseDuration>0</NewLeaseDuration>
                """.trimIndent()
            )
            true
        }.getOrElse { e ->
            GameEngine.log("UPnP add port mapping failed: ${e.message}")
            false
        }
    }

    private fun soapRequest(controlUrl: URL, serviceType: String, action: String, body: String): ByteArray {
        val envelope = """
            <?xml version="1.0"?>
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
              <s:Body>
                <u:$action xmlns:u="$serviceType">
                  $body
                </u:$action>
              </s:Body>
            </s:Envelope>
        """.trimIndent().toByteArray(Charsets.UTF_8)

        val connection = controlUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"")
        connection.setRequestProperty("SOAPAction", "\"$serviceType#$action\"")
        connection.outputStream.use { it.write(envelope) }
        val code = connection.responseCode
        val response = if (code in 200..299) connection.inputStream else connection.errorStream
        val bytes = response?.use { it.readBytes() } ?: ByteArray(0)
        if (code !in 200..299) {
            throw IOException("SOAP $action failed with HTTP $code: ${String(bytes, Charsets.UTF_8)}")
        }
        return bytes
    }

    private fun httpGet(url: URL): ByteArray {
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        return connection.inputStream.use { it.readBytes() }
    }

    private fun getLocalAddressFor(host: String): InetAddress? {
        val socket = DatagramSocket()
        return try {
            socket.connect(InetSocketAddress(host, 1900))
            socket.localAddress
        } finally {
            socket.close()
        }
    }

    private fun resolveUrl(base: URL, value: String): URL {
        val uri = URI(value)
        if (uri.isAbsolute) return uri.toURL()
        return base.toURI().resolve(value).toURL()
    }

    private fun documentBuilder(): javax.xml.parsers.DocumentBuilder {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = false
        runCatching { factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true) }
        runCatching { factory.setFeature("http://xml.org/sax/features/external-general-entities", false) }
        runCatching { factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false) }
        return factory.newDocumentBuilder()
    }

    private fun Element.childText(tagName: String): String? {
        return getElementsByTagName(tagName).item(0)?.textContent?.trim()
    }
}
