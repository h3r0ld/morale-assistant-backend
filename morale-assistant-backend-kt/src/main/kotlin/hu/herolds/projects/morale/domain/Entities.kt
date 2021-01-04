package hu.herolds.projects.morale.domain

import hu.herolds.projects.morale.domain.enums.Language
import java.net.URI
import javax.persistence.*

@MappedSuperclass
sealed class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
)

@Entity(name = "joke")
class Joke(
    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    var language: Language? = null,
    @Column(name = "text", nullable = false)
    var text: String? = null,
    @Column(name = "sound_file_path")
    var soundFilePath: URI? = null
): BaseEntity()

