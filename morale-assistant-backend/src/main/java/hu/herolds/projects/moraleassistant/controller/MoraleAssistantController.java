package hu.herolds.projects.moraleassistant.controller;

import hu.herolds.projects.moraleassistant.controller.dto.JokeDto;
import hu.herolds.projects.moraleassistant.model.enums.Language;
import hu.herolds.projects.moraleassistant.service.JokeManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/joke/")
public class MoraleAssistantController {
    private final JokeManagerService jokeManagerService;

    public MoraleAssistantController(JokeManagerService jokeManagerService) {
        this.jokeManagerService = jokeManagerService;
    }


    @GetMapping("/{lang}/")
    public ResponseEntity<JokeDto> getNextJoke(@PathVariable(name = "lang") final Language language) {
        return ResponseEntity.ok(jokeManagerService.getRandomJoke(language));
    }
}
