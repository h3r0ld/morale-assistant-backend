package hu.herolds.projects.morale.service.synthetize

import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.domain.enums.Language.EN
import hu.herolds.projects.morale.exception.SynthesizeException
import marytts.LocalMaryInterface
import marytts.util.data.audio.MaryAudioUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.sound.sampled.AudioInputStream
import kotlin.io.path.createTempFile
import kotlin.io.path.readBytes

@Component("MaryTTSSynthesizerUtil")
class MaryTTSSynthesizer(
    private val localMaryInterface: LocalMaryInterface,
): Synthesizer {
    private val log = LoggerFactory.getLogger(javaClass)

    override val supportedLanguages: Set<Language> = setOf(EN)

    override fun synthesize(text: String): ByteArray {
        try {
            log.info("Synthesizing text: [$text]")
            val audio: AudioInputStream = localMaryInterface.generateAudio(text)

            val samples = MaryAudioUtils.getSamplesAsDoubleArray(audio)

            val audioFilePath = createTempFile(prefix = "sound", suffix = ".wav").toAbsolutePath()

            MaryAudioUtils.writeWavFile(samples, audioFilePath.toString(), audio.format)
            log.info("Audio content written to file: [$audioFilePath]")
            return audioFilePath.toFile().apply {
                deleteOnExit()
            }.readBytes()
        } catch (ex: Exception) {
            log.error("Could not synthesize and save audio file.", ex)
            throw SynthesizeException(cause = ex)
        }
    }
}