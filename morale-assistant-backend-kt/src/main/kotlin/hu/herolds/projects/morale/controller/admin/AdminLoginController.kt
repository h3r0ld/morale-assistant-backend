package hu.herolds.projects.morale.controller.admin

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/admin/login", produces = [APPLICATION_JSON_VALUE])
class AdminLoginController {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping
    fun login(@AuthenticationPrincipal user: UserDetails): UserDetails {
        log.info("Logged in admin user: [${user.username}]")
        return user
    }
}
