package hu.herolds.projects.moraleassistant.config;

import hu.herolds.projects.moraleassistant.params.MoraleAssistantParameters;
import hu.herolds.projects.moraleassistant.service.SynthesizerService;
import hu.herolds.projects.moraleassistant.util.synthesizer.Synthesizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private SynthesizerService synthesizerService;
    @Autowired
    private MoraleAssistantParameters moraleAssistantParameters;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        try {
            initSoundFilesBaseFolder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ApplicationContext context = event.getApplicationContext();

        final Stream<Synthesizer> synthesizerServices = context.getBeansOfType(Synthesizer.class).values().stream();

        synthesizerService.initialize(synthesizerServices.collect(Collectors.toList()));
    }

    private void initSoundFilesBaseFolder() throws IOException {
        final Path filePath = Paths.get(moraleAssistantParameters.getSounds().getBasePath().toString());
        if (!Files.exists(filePath)) {
            log.info("Created directory for sound files: {}", filePath);
            Files.createDirectory(filePath);
        } else {
            log.info("Using existing directory for sound files: {}", filePath);
        }
    }
}
