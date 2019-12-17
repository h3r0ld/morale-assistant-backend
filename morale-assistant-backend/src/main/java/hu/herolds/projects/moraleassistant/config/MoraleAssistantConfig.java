package hu.herolds.projects.moraleassistant.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import hu.herolds.projects.moraleassistant.params.MoraleAssistantParameters;
import lombok.extern.slf4j.Slf4j;
import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class MoraleAssistantConfig {

    @Bean
    public LocalMaryInterface localMaryInterface() throws MaryConfigurationException {
        log.info("Initializing Mary TTS...");
        final LocalMaryInterface localMaryInterface = new LocalMaryInterface();

        log.info("Available locales: {}", localMaryInterface.getAvailableLocales());
        log.info("Available voices: {}", localMaryInterface.getAvailableVoices());
        return localMaryInterface;
    }

    @Bean
    public TextToSpeechClient textToSpeechClient(final MoraleAssistantParameters moraleAssistantParameters) throws IOException {
        final GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(moraleAssistantParameters.getGoogleCredentialsFile().getFile()))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        final TextToSpeechSettings textToSpeechSettings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        return TextToSpeechClient.create(textToSpeechSettings);
    }
}
