package hu.herolds.projects.morale.domain

import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.exception.SoundFileNotFoundException
import hu.herolds.projects.morale.util.toByteArray
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.net.URI
import java.time.LocalDateTime
import javax.persistence.*

@MappedSuperclass
open class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
)

@Entity(name = "joke")
class Joke(
    @Enumerated(EnumType.STRING)
    var language: Language,
    var text: String,
    var soundFilePath: URI? = null,
    @CreationTimestamp
    var created: LocalDateTime = LocalDateTime.now(),
    @UpdateTimestamp
    var lastModified: LocalDateTime = LocalDateTime.now(),
): BaseEntity() {

    fun getSoundFile(): ByteArray? = try {
        soundFilePath?.toByteArray()
    } catch (ex: Exception) {
        throw SoundFileNotFoundException(joke = this, message = "The sound file for this joke could not be found!", cause = ex)
    }
}

