package hu.herolds.projects.morale.config

import hu.herolds.projects.morale.domain.AdminUser
import hu.herolds.projects.morale.repository.AdminUserRepository
import hu.herolds.projects.morale.service.SynthesizerService
import hu.herolds.projects.morale.service.synthetize.Synthesizer
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Component
class ApplicationInitializer(
    private val synthesizerService: SynthesizerService,
    private val adminUserRepository: AdminUserRepository,
    private val applicationParameters: ApplicationParameters,
): ApplicationListener<ContextRefreshedEvent> {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        try {
            initSoundFilesBaseFolder()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val synthesizers = event.applicationContext.getBeansOfType(Synthesizer::class.java).values.toList()
        synthesizerService.initialize(synthesizers)

        if (adminUserRepository.findByUsername("admin") == null) {
            adminUserRepository.save(AdminUser(username = "admin", password = BCryptPasswordEncoder().encode("admin")))
        }
    }

    private fun initSoundFilesBaseFolder() {
        val filePath: Path = applicationParameters.baseDirectory
        if (!Files.exists(filePath)) {
            log.info("Created directory for sound files: {}", filePath)
            Files.createDirectory(filePath)
        } else {
            log.info("Using existing directory for sound files: {}", filePath)
        }
    }
}