package hu.herolds.projects.morale.repository

import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.domain.enums.Language
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JokeRepository : JpaRepository<Joke, UUID>, JpaSpecificationExecutor<Joke> {
    fun findByLanguageAndSoundFilePathNotNull(language: Language, pageable: Pageable): Page<Joke>
    fun countByLanguage(language: Language): Long
}
