package hu.herolds.projects.morale.domain

import hu.herolds.projects.morale.domain.enums.Language
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.UpdateTimestamp
import java.net.URI
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@MappedSuperclass
open class BaseEntity(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    var id: UUID? = null,
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
): BaseEntity()

@Entity
class AdminUser(
        @Column(name = "username", nullable = false)
        var username: String,
        @Column(name = "password", nullable = false)
        var password: String,
): BaseEntity()
