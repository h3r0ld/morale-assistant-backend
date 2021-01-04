package hu.herolds.projects.moraleassistant.util;

import hu.herolds.projects.moraleassistant.params.MoraleAssistantParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    public Path getBaseDirectory() {
        return Paths.get(moraleAssistantParameters.getSounds().getBasePath().toString());
    }

    public Path getNextFilePath() {
        return getBaseDirectory().resolve(UUID.randomUUID().toString() +  ".wav");
    }
}
