package hu.herolds.projects.morale.controller.dto

import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.util.toByteArray
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Null

class JokeDto(
    @get:NotBlank
    val text: String,
    @get:NotNull
    val language: Language,
    @get:Null
    val id: Long? = null,
    @get:Null
    val created: LocalDateTime? = null,
    @get:Null
    val lastModified: LocalDateTime? = null,
    @get:Null
    var soundFile: ByteArray? = null,
)

fun Joke.mapToJokeDto(withSoundFile: Boolean = false): JokeDto = JokeDto(
    id = this.id,
    language = this.language,
    text = this.text,
    created = this.created,
    lastModified = this.lastModified

).also { dto ->
    if (withSoundFile) {
        dto.soundFile = this.soundFilePath.toByteArray()
    }
}