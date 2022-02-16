package hu.herolds.projects.morale.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.GroupedOpenApi
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(
    name = "basicAuth",
    type = HTTP,
    scheme = "basic",
)
class OpenApiConfig {

    @Bean
    fun openApi(buildProperties: BuildProperties): OpenAPI = OpenAPI()
        .components(Components())
        .info(
            Info()
            .title("Morale Assistant API")
            .description("A RESTful service for boosting your morale.")
            .version(buildProperties.version)
            .contact(
                Contact()
                .name("Kristof Herold")
                .url("https://github.com/h3r0ld")
                .email("kristof.herold@gmail.com")
            )
        )

    @Bean
    fun publicOpenApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/api/public/**")
        .build()

    @Bean
    fun adminOpenApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("admin")
        .pathsToMatch("/api/admin/**")
        .build()
}