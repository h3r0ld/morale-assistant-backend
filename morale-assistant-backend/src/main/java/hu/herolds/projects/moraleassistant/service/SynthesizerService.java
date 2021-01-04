package hu.herolds.projects.moraleassistant.service;

import hu.herolds.projects.moraleassistant.exception.SynthesizeException;
import hu.herolds.projects.moraleassistant.model.enums.Language;
import hu.herolds.projects.moraleassistant.util.synthesizer.Synthesizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
public class SynthesizerService {
    private Map<Language, Set<Synthesizer>> synthesizerServiceMap;

    public void initialize(final List<Synthesizer> synthesizers) {
        synthesizerServiceMap = new HashMap<>();

        synthesizers.forEach(synthesizerService -> synthesizerService.getSupportedLanguages().forEach(language -> {
            if (!synthesizerServiceMap.containsKey(language)) {
                synthesizerServiceMap.put(language, new HashSet<>());
            }
            synthesizerServiceMap.get(language).add(synthesizerService);
            log.info("Registered synthesizer service to language {}: {}", language, synthesizerService.getClass().getSimpleName());
        }));
    }

    public Path synthesize(final Language language, final String text) throws SynthesizeException {
        final Synthesizer synthesizer = getSynthesizerService(language);
        return synthesizer.synthesize(text);
    }

    private Synthesizer getSynthesizerService(final Language language) throws SynthesizeException {
        final Set<Synthesizer> synthesizers = synthesizerServiceMap.get(language);

        if (CollectionUtils.isEmpty(synthesizers)) {
            throw new SynthesizeException("Language is not supported: " + language);
        }

        return new ArrayList<>(synthesizers).get(0);
    }
}
