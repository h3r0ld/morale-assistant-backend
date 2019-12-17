package hu.herolds.projects.moraleassistant.params;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Data
@Configuration
@ConfigurationProperties
public class MoraleAssistantParameters {
    private SoundsParams sounds;
    private Resource googleCredentialsFile;
}
