package hu.herolds.projects.moraleassistant.util.synthesizer;

import hu.herolds.projects.moraleassistant.exception.SynthesizeException;
import hu.herolds.projects.moraleassistant.model.enums.Language;
import hu.herolds.projects.moraleassistant.util.SoundFilesUtils;
import lombok.extern.slf4j.Slf4j;
import marytts.LocalMaryInterface;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.MaryAudioUtils;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component("MaryTTSSynthesizerUtil")
public class MaryTTSSynthesizer implements Synthesizer {
    private final LocalMaryInterface localMaryInterface;
    private final SoundFilesUtils soundFilesUtils;

    public MaryTTSSynthesizer(
            final LocalMaryInterface localMaryInterface,
            final SoundFilesUtils soundFilesUtils) {
        this.localMaryInterface = localMaryInterface;
        this.soundFilesUtils = soundFilesUtils;
    }

    public Path synthesize(final String text) throws SynthesizeException {
        // synthesize
        final AudioInputStream audio;
        try {
            audio = localMaryInterface.generateAudio(text);
        } catch (SynthesisException e) {
            throw new SynthesizeException(e);
        }

        // write to output
        double[] samples = MaryAudioUtils.getSamplesAsDoubleArray(audio);


        Path resolve = null;
        try {
            resolve = soundFilesUtils.getNextFilePath();
            log.info("File path to write: [{}]", resolve.toString());
            MaryAudioUtils.writeWavFile(samples, resolve.toString(), audio.getFormat());
            log.info("Output written to: [{}]", resolve.toString());
            return resolve;
        } catch (final IOException e) {
            log.error("Could not write to file: " + (resolve != null ? resolve.toString() : ""), e);
            throw new SynthesizeException(e);
        }

    }

    @Override
    public Set<Language> getSupportedLanguages() {
        return Stream.of(Language.EN).collect(Collectors.toSet());
    }

}
