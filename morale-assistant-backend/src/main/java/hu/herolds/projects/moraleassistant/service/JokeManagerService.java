package hu.herolds.projects.moraleassistant.service;

import hu.herolds.projects.moraleassistant.controller.dto.JokeDto;
import hu.herolds.projects.moraleassistant.model.Joke;
import hu.herolds.projects.moraleassistant.model.enums.Language;
import hu.herolds.projects.moraleassistant.repository.JokeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JokeManagerService {
    private final SynthesizerService synthesizerService;
    private final JokeRepository jokeRepository;

    // TODO: You sure about that?
    private static final Map<String, Set<Long>> JOKES_BY_SESSION_ID = new HashMap<>();

    public JokeManagerService(SynthesizerService synthesizerService, JokeRepository jokeRepository) {
        this.synthesizerService = synthesizerService;
        this.jokeRepository = jokeRepository;
    }

    public void saveJoke(final JokeDto jokeDto) {
        final Joke joke = Joke.builder()
                .language(jokeDto.getLanguage())
                .text(jokeDto.getText())
                .build();

        jokeRepository.save(joke);
    }

    public JokeDto getRandomJoke(final Language language) {
        final String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        log.info("Getting a random joke for user with sessionId: [{}], language: [{}]", sessionId, language);
        final Set<Long> prevJokeIds = JOKES_BY_SESSION_ID.getOrDefault(sessionId, new HashSet<>());
        log.info("Jokes that the [{}] sessionId user already heard: [{}]", sessionId, prevJokeIds);

        final List<Joke> possibleJokes = jokeRepository.findByLanguageAndIdNotIn(language, prevJokeIds);
        log.info("Number of possible jokes for the next one: [{}]", possibleJokes.size());


        // If the user heard all the jokes, then we play all of them again for him/her ...
        if (possibleJokes.isEmpty()) {
            log.info("User heard all of them already, getting random joke from all jokes for sessionId: [{}]", sessionId);
            // JOKES_BY_SESSION_ID.put(sessionId, new HashSet<>());
            possibleJokes.addAll(jokeRepository.findAll());
        }

        final int nextJokeIndex = new Random().nextInt(possibleJokes.size());
        final Joke nextJoke = possibleJokes.get(nextJokeIndex);

        prevJokeIds.add(nextJoke.getId());
        JOKES_BY_SESSION_ID.put(sessionId, prevJokeIds);

        return mapToJokeDto(nextJoke);
    }

    public List<JokeDto> getJokes() {
        return jokeRepository.findAll().stream().map(this::mapToJokeDto)
                .collect(Collectors.toList());
    }

    private JokeDto mapToJokeDto(final Joke joke) {
        byte[] soundFile = null;
        try {
            soundFile = IOUtils.toByteArray(joke.getSoundFilePath());
        } catch (IOException e) {
            log.error("Could not load sound file for joke.", e);
        }

        return JokeDto.builder()
                .id(joke.getId())
                .language(joke.getLanguage())
                .text(joke.getText())
                .path(joke.getSoundFilePath().toString())
                .soundFile(soundFile)
                .build();
    }
}
