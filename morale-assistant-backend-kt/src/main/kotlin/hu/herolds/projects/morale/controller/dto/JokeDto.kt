package hu.herolds.projects.morale.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.domain.enums.Language
import java.time.LocalDateTime
import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Null

data class JokeDto(
    @get:NotBlank
    val text: String,
    @get:NotNull
    val language: Language,
    @get:JsonProperty(access = READ_ONLY)
    val id: UUID? = null,
    @get:JsonProperty(access = READ_ONLY)
    val created: LocalDateTime? = null,
    @get:JsonProperty(access = READ_ONLY)
    val lastModified: LocalDateTime? = null,
    @get:JsonProperty(access = READ_ONLY)
    var soundFile: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JokeDto

        if (text != other.text) return false
        if (language != other.language) return false
        if (id != other.id) return false
        if (created != other.created) return false
        if (lastModified != other.lastModified) return false
        if (soundFile != null) {
            if (other.soundFile == null) return false
            if (!soundFile.contentEquals(other.soundFile)) return false
        } else if (other.soundFile != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (created?.hashCode() ?: 0)
        result = 31 * result + (lastModified?.hashCode() ?: 0)
        result = 31 * result + (soundFile?.contentHashCode() ?: 0)
        return result
    }
}

fun Joke.mapToJokeDto() = JokeDto(
    id = this.id,
    language = this.language,
    text = this.text,
    created = this.created,
    lastModified = this.lastModified
)
