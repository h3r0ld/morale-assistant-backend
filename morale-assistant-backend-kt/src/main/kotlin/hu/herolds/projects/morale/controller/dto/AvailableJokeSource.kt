package hu.herolds.projects.morale.controller.dto

import hu.herolds.projects.morale.domain.enums.Language
import java.net.URL

enum class AvailableJokeSource {
    CHUCK_NORRIS_API,
    SV443_JOKE_API
}

data class JokeSource(
    val name: AvailableJokeSource,
    val url: URL,
    val language: Language
)