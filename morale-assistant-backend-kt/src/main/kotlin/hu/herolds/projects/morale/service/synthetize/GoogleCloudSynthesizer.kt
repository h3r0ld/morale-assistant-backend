package hu.herolds.projects.morale.service.synthetize

import com.google.cloud.texttospeech.v1.AudioConfig
import com.google.cloud.texttospeech.v1.AudioEncoding
import com.google.cloud.texttospeech.v1.SsmlVoiceGender
import com.google.cloud.texttospeech.v1.SynthesisInput
import com.google.cloud.texttospeech.v1.TextToSpeechClient
import com.google.cloud.texttospeech.v1.VoiceSelectionParams
import hu.herolds.projects.morale.config.ApplicationParameters
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.domain.enums.Language.HU
import hu.herolds.projects.morale.exception.SynthesizeException
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.nio.file.Path

// https://cloud.google.com/text-to-speech/docs/quickstart-client-libraries#client-libraries-install-java
@Service
@ConditionalOnBean(TextToSpeechClient::class)
class GoogleCloudSynthesizer(
    private val textToSpeechClient: TextToSpeechClient,
): Synthesizer {
    private val log = LoggerFactory.getLogger(javaClass)

    override val supportedLanguages: Set<Language> = setOf(HU)

    override fun synthesize(text: String): ByteArray {
        try {
            log.info("Synthesizing text: [$text]")

            // Set the text input to be synthesized
            val input = SynthesisInput.newBuilder()
                .setText(text)
                .build()

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            val voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode(HU.languageCode)
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build()

            // Select the type of audio file you want returned
            val audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build()

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            val response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig)

            return response.audioContent.toByteArray()
        } catch (ex: Exception) {
            log.error("Could not synthesize and save audio file.", ex)
            throw SynthesizeException(cause = ex)
        }
    }
}
