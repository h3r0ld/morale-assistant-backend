package hu.herolds.projects.morale.controller.dto

import hu.herolds.projects.morale.domain.enums.Language

class JokeDto(
    val id: Long? = null,
    // @get:NotBlank
    val text: String? = null,
    // @get:NotNull
    val language: Language? = null,
    // @get:Null
    private val path: String? = null,
    // @get:Null
    val soundFile: ByteArray?
)