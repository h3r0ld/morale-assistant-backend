package hu.herolds.projects.morale.controller.dto

import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.util.toByteArray
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Null

class JokeDto(
    @get:Null
    val id: Long? = null,
    @get:NotBlank
    val text: String? = null,
    @get:NotNull
    val language: Language? = null,
    @get:Null
    var soundFile: ByteArray? = null,
)

fun Joke.mapToJokeDto(withSoundFile: Boolean = false): JokeDto = JokeDto(
    id = this.id,
    language = this.language,
    text = this.text,
).also { dto ->
    if (withSoundFile) {
        dto.soundFile = this.soundFilePath.toByteArray()
    }
}