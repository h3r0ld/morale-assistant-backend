package hu.herolds.projects.morale

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MoraleAssistantApplication

fun main(args: Array<String>) {
  runApplication<MoraleAssistantApplication>(*args)
}
