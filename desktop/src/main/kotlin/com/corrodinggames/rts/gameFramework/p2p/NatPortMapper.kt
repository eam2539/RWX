package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
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
import javax.xml.parsers.DocumentBuilderFactory

class NatPortMapper {
    data class Mapping(
        val externalIp: String,
        val externalPort: Int,
        val internalPort: Int,
        val protocol: String,
        val controlUrl: URL,
        val serviceType: String
    )

    private data class UpnpService(
        val controlUrl: URL,
        val serviceType: String
    )

    fun mapTcpPort(internalPort: Int, description: String): Mapping? {
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
            controlUrl = service.controlUrl,
            serviceType = service.serviceType
        )
    }

    fun deleteMapping(mapping: Mapping) {
        runCatching {
            soapRequest(
                controlUrl = mapping.controlUrl,
                serviceType = mapping.serviceType,
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
