package hu.herolds.projects.morale.config

import io.awspring.cloud.autoconfigure.context.ContextCredentialsAutoConfiguration
import io.awspring.cloud.autoconfigure.context.ContextRegionProviderAutoConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!aws-prod")
@Configuration
@EnableAutoConfiguration(exclude = [
    ContextCredentialsAutoConfiguration::class,
    ContextRegionProviderAutoConfiguration::class
])
class AwsConfig