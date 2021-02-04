package hu.herolds.projects.morale.service

import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.exception.SynthesizeException
import hu.herolds.projects.morale.service.synthetize.Synthesizer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Path

@Service
class SynthesizerService {
    private var synthesizerMap: MutableMap<Language, MutableSet<Synthesizer>> = mutableMapOf()

    fun initialize(synthesizers: List<Synthesizer>) {
        synthesizers.forEach {synthesizer: Synthesizer ->
            synthesizer.supportedLanguages.forEach { language ->
                synthesizerMap.computeIfAbsent(language) {
                    mutableSetOf()
                }

                synthesizerMap[language]!!.add(synthesizer)
                log.info("Registered synthesizer service to language $language: ${synthesizer.javaClass.simpleName}")
            }
        }
    }

    fun synthesize(language: Language, text: String): Path = getSynthesizerService(language).synthesize(text)

    private fun getSynthesizerService(language: Language): Synthesizer = synthesizerMap[language]?.first()
            ?: throw SynthesizeException("Language is not supported: $language")

    companion object {
        private val log = LoggerFactory.getLogger(SynthesizerService::class.java)
    }
}