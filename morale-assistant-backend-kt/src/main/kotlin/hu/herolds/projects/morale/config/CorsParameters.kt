package hu.herolds.projects.morale.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.http.HttpMethod
import java.net.URI
import java.net.URL

@ConstructorBinding
@ConfigurationProperties(prefix = "cors")
data class CorsParameters(
    val allowCredentials: Boolean?,
    val allowedOrigins: List<URL> = listOf(),
    val allowedMethods: List<HttpMethod> = HttpMethod.values().toList(),
    val allowedHeaders: List<String> = listOf()
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val allowedOriginsParams = allowedOrigins.map { it.toString() }.toTypedArray()
    val allowedMethodsParams = allowedMethods.map { it.name }.toTypedArray()
    val allowedHeadersParams = allowedHeaders.toTypedArray()

    init {
        log.info("[CORS Configuration] - allow-credentials = $allowCredentials")
        log.info("[CORS Configuration] - allowed-origins = $allowedOrigins")
        log.info("[CORS Configuration] - allowed-methods = $allowedMethods")
        log.info("[CORS Configuration] - allowed-headers = $allowedHeaders")
    }
}
