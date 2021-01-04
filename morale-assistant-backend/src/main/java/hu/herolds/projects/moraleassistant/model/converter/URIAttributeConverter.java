package hu.herolds.projects.moraleassistant.model.converter;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Converter(autoApply = true)
public class URIAttributeConverter implements AttributeConverter<URI, String> {

    @Override
    public String convertToDatabaseColumn(URI uri) {
        return uri != null ? uri.getPath() : null;
    }

    @Override
    public URI convertToEntityAttribute(String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            log.error("Could not convert to entity attribute (URI)", e);
            return null;
        }
    }
}
