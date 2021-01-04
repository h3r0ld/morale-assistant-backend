package hu.herolds.projects.moraleassistant.controller.admin;

import hu.herolds.projects.moraleassistant.controller.dto.JokeDto;
import hu.herolds.projects.moraleassistant.exception.SynthesizeException;
import hu.herolds.projects.moraleassistant.service.JokeManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/joke/")
public class JokeManagerController {
    private final JokeManagerService jokeManagerService;

    public JokeManagerController(JokeManagerService jokeManagerService) {
        this.jokeManagerService = jokeManagerService;
    }

    @PostMapping
    public void saveJoke(@RequestBody @Validated final JokeDto jokeDto) throws SynthesizeException {
        jokeManagerService.saveJoke(jokeDto);
    }

    @GetMapping
    public ResponseEntity<List<JokeDto>> getJokes() {
        return ResponseEntity.ok(jokeManagerService.getJokes());
    }
}
