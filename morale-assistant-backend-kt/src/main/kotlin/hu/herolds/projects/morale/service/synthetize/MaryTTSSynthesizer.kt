package hu.herolds.projects.morale.service.synthetize

import hu.herolds.projects.morale.config.ApplicationParameters
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.domain.enums.Language.EN
import hu.herolds.projects.morale.exception.SynthesizeException
import marytts.LocalMaryInterface
import marytts.util.data.audio.MaryAudioUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.file.Path
import javax.sound.sampled.AudioInputStream

@Component("MaryTTSSynthesizerUtil")
class MaryTTSSynthesizer(
    private val localMaryInterface: LocalMaryInterface,
    private val applicationParameters: ApplicationParameters
): Synthesizer {
    override val supportedLanguages: Set<Language> = setOf(EN)

    override fun synthesize(text: String): Path {
        try {
            val audio: AudioInputStream = localMaryInterface.generateAudio(text)

            val samples = MaryAudioUtils.getSamplesAsDoubleArray(audio)
            val audioFilePath = applicationParameters.getNextFilePath()

            log.debug("File path to write: [$audioFilePath]")
            MaryAudioUtils.writeWavFile(samples, audioFilePath.toString(), audio.format)
            log.debug("Output written to: [$audioFilePath]")
            return audioFilePath
        } catch (ex: Exception) {
            log.error("Could not synthesize and save audio file.", ex)
            throw SynthesizeException(ex)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(MaryTTSSynthesizer::class.java)
    }
}