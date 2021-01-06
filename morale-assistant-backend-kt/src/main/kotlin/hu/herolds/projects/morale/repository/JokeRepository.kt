package hu.herolds.projects.morale.repository

import hu.herolds.projects.morale.domain.Joke
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface JokeRepository : JpaRepository<Joke, Long>, JpaSpecificationExecutor<Joke> {}