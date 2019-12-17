package hu.herolds.projects.moraleassistant.service.synthesizer;

import hu.herolds.projects.moraleassistant.exception.SynthesizeException;

import java.nio.file.Path;

public interface SynthesizerService {
    Path synthesize(final String text) throws SynthesizeException;
}
