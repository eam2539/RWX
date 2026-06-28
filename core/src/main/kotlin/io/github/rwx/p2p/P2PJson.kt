package io.github.rwx.p2p

import kotlinx.serialization.json.Json

val P2PJson: Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
