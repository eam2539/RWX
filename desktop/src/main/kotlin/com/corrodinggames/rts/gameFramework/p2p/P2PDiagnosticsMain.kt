package com.corrodinggames.rts.gameFramework.p2p

import java.net.Socket
import kotlin.system.exitProcess

object P2PDiagnosticsMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val mode = args.firstOrNull() ?: "discover"
        val timeoutMs = args.getOrNull(1)?.toLongOrNull() ?: 60000L
        when (mode) {
            "discover" -> discover(timeoutMs)
            "join-first" -> joinFirst(timeoutMs)
            "github-publish-test" -> githubPublishTest(timeoutMs)
            "github-delete-test" -> githubDeleteTest(timeoutMs)
            else -> {
                println("Usage: p2pDiagnostics [discover|join-first|github-publish-test|github-delete-test] [timeoutMs]")
                exitProcess(2)
            }
        }
    }

    private fun discover(timeoutMs: Long) {
        val lobby = P2PLobbyService.getInstance()
        try {
            lobby.startIfNeeded()
            val rooms = waitForRooms(lobby, timeoutMs)
            println("P2P_DIAG rooms=${rooms.size}")
            rooms.forEachIndexed { index, room ->
                println("P2P_DIAG room[$index] id=${room.roomId} host=${room.hostPeerId} by=${room.createdBy} state=${room.gameState} map=${room.mapPath}")
                println("P2P_DIAG room[$index] webrtc=${room.webrtcSignaling} ice=${room.webrtcIceServers.joinToString(",")}")
                println("P2P_DIAG room[$index] direct=${room.libp2pDirectAddresses.joinToString(",")}")
                println("P2P_DIAG room[$index] mapped=${room.libp2pMappedAddresses.joinToString(",")}")
            }
            if (rooms.isEmpty()) exitProcess(1)
        } finally {
            lobby.stopSession()
        }
        exitProcess(0)
    }

    private fun joinFirst(timeoutMs: Long) {
        val lobby = P2PLobbyService.getInstance()
        try {
            lobby.startIfNeeded()
            val room = waitForRooms(lobby, timeoutMs).firstOrNull() ?: run {
                println("P2P_DIAG no rooms discovered")
                exitProcess(1)
            }
            println("P2P_DIAG joining room id=${room.roomId} host=${room.hostPeerId}")
            val address = lobby.prepareJoin(room.roomId!!)
            println("P2P_DIAG local proxy=$address")
            val host = address.substringBefore(':')
            val port = address.substringAfter(':').toInt()
            val socket = Socket(host, port)
            try {
                socket.tcpNoDelay = true
                socket.soTimeout = timeoutMs.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                println("P2P_DIAG connected to local proxy")
                Thread.sleep(timeoutMs.coerceAtMost(15000L))
                println("P2P_DIAG join probe completed")
            } finally {
                socket.close()
            }
        } finally {
            lobby.stopSession()
        }
        exitProcess(0)
    }

    private fun waitForRooms(lobby: P2PLobbyService, timeoutMs: Long): List<P2PRoomAdvertisement> {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            val rooms = lobby.getRooms()
            if (rooms.isNotEmpty()) return rooms
            lobby.requestRefresh()
            Thread.sleep(1000L)
        }
        return lobby.getRooms()
    }

    private fun githubPublishTest(timeoutMs: Long) {
        val room = createGithubDiagnosticRoom(timeoutMs)
        val publisher = githubPublisherOrExit()
        publisher.publishRoom(room)
        println("P2P_DIAG github publish requested room=${room.roomId}")
        exitProcess(0)
    }

    private fun githubDeleteTest(timeoutMs: Long) {
        val room = createGithubDiagnosticRoom(timeoutMs)
        val publisher = githubPublisherOrExit()
        publisher.closeRoom(room)
        println("P2P_DIAG github delete requested room=${room.roomId}")
        exitProcess(0)
    }

    private fun githubPublisherOrExit(): P2PGithubGistPublisher {
        val config = P2PConfigLoader.load().discovery.github.publish
        val publisher = P2PGithubGistPublisher(config)
        if (!publisher.isEnabled()) {
            println("P2P_DIAG github publish disabled or missing token")
            exitProcess(1)
        }
        return publisher
    }

    private fun createGithubDiagnosticRoom(timeoutMs: Long): P2PRoomAdvertisement {
        val now = System.currentTimeMillis()
        return P2PRoomAdvertisement().apply {
            roomId = "diag-${now}"
            hostPeerId = "QmDiagGithubPublishTest"
            createdBy = "diagnostics"
            gameVersionCode = 1
            gameVersionString = "diagnostics"
            mapPath = "diagnostics"
            gameState = "battleroom"
            webrtcSignaling = "gossip"
            expiresAtMs = now + timeoutMs.coerceAtLeast(30000L)
            seq = 1
        }
    }
}
