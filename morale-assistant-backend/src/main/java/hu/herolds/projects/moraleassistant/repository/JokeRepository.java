package hu.herolds.projects.moraleassistant.repository;

import hu.herolds.projects.moraleassistant.model.Joke;
import hu.herolds.projects.moraleassistant.model.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface JokeRepository extends JpaRepository<Joke, Long> {
    List<Joke> findByLanguageAndIdNotIn(final Language language, final Set<Long> ids);
}
