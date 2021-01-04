package hu.herolds.projects.moraleassistant.util.synthesizer;

import hu.herolds.projects.moraleassistant.exception.SynthesizeException;
import hu.herolds.projects.moraleassistant.model.enums.Language;

import java.nio.file.Path;
import java.util.Set;

public interface Synthesizer {
    Path synthesize(final String text) throws SynthesizeException;
    Set<Language> getSupportedLanguages();
}
