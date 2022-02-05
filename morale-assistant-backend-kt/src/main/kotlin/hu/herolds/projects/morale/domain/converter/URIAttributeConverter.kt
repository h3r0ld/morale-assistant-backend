package hu.herolds.projects.morale.domain.converter

import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URISyntaxException
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
internal class URIAttributeConverter : AttributeConverter<URI?, String?> {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun convertToDatabaseColumn(uri: URI?): String? {
        return uri?.path
    }

    override fun convertToEntityAttribute(str: String?): URI? {
        return try {
            str?.let {
                URI(it)
            }
        } catch (e: URISyntaxException) {
            log.error("Could not convert to entity attribute (URI)", e)
            null
        }
    }
}