package hu.herolds.projects.moraleassistant.util;

import hu.herolds.projects.moraleassistant.params.MoraleAssistantParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
public class SoundFilesUtils {
    private final MoraleAssistantParameters moraleAssistantParameters;

    public SoundFilesUtils(MoraleAssistantParameters moraleAssistantParameters) {
        this.moraleAssistantParameters = moraleAssistantParameters;
    }

    public Path getBaseDirectory() throws IOException {
        final Path filePath = Paths.get(moraleAssistantParameters.getSounds().getBasePath().toString());

        if (!Files.exists(filePath)) {
            log.info("Created directory for sound files: {}", filePath);
            // TODO: Move folder creation to event handler after context initialization
            return Files.createDirectory(filePath);
        } else {
            log.debug("Using existing directory for sound files: {}", filePath);
            return filePath;
        }
    }

    public Path getNextFilePath() throws IOException {
        return getBaseDirectory().resolve(UUID.randomUUID().toString() +  ".wav");
    }
}
