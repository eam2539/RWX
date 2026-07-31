package io.github.rwx.slick

import com.corrodinggames.rts.gameFramework.network.PasswordHandler
import io.github.rwx.logger
import io.github.rwx.ui.CoreUiEventQueue

internal class SlickTextInputBridge {
    fun deliver(game: SlickGame, request: SlickTextInputRequest): Boolean {
        enqueue(game, request)
        return true
    }

    fun poll(game: SlickGame) {
        val requests = runCatching { game.pollTextInputRequests() }
            .onFailure { error -> logger.warn(error) { "Unable to read Slick UI requests" } }
            .getOrDefault(emptyList())
        requests.forEach { request -> enqueue(game, request) }
    }

    private fun enqueue(game: SlickGame, request: SlickTextInputRequest) {
        CoreUiEventQueue.requestPasswordDialog(
            object : PasswordHandler() {
                override fun submitPassword(str: String?) {
                    game.submitTextInput(request.id,str.orEmpty())
                }

                override fun cancelPasswordEntry() {
                    game.cancelTextInput(request.id)
                }
            },
            request.title,
            request.prompt,
            request.confirmButtonLabel,
            request.cancelButtonLabel,
        )
    }
}

