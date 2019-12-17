package hu.herolds.projects.moraleassistant.service.synthesizer;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import hu.herolds.projects.moraleassistant.exception.SynthesizeException;
import hu.herolds.projects.moraleassistant.util.SoundFilesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

// https://cloud.google.com/text-to-speech/docs/quickstart-client-libraries#client-libraries-install-java
@Slf4j
@Component("GoogleSynthesizerUtils")
public class GoogleSynthesizerService implements SynthesizerService {
    private final TextToSpeechClient textToSpeechClient;
    private final SoundFilesUtils soundFilesUtils;


    public GoogleSynthesizerService(final TextToSpeechClient textToSpeechClient,
                                    final SoundFilesUtils soundFilesUtils) {
        this.textToSpeechClient = textToSpeechClient;
        this.soundFilesUtils = soundFilesUtils;
    }

    @Override
    public Path synthesize(String text) throws SynthesizeException {
        // Set the text input to be synthesized
        SynthesisInput input = SynthesisInput.newBuilder()
                .setText(text)
                .build();

        // Build the voice request, select the language code ("en-US") and the ssml voice gender
        // ("neutral")
        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("hu")
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build();

        // Select the type of audio file you want returned
        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();

        // Perform the text-to-speech request on the text input with the selected voice parameters and
        // audio file type
        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
                audioConfig);

        // Get the audio contents from the response
        ByteString audioContents = response.getAudioContent();

        try {
            final Path nextFilePath = soundFilesUtils.getNextFilePath();
            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream(nextFilePath.toFile())) {
                out.write(audioContents.toByteArray());
                log.info("Audio content written to file: [{}]", nextFilePath.toString());
                return nextFilePath;
            }
        } catch (IOException e) {
            throw new SynthesizeException(e);
        }
    }
}
