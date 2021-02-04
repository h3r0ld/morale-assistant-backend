package hu.herolds.projects.morale.controller.admin

import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/login")
class AdminLoginController {

    @GetMapping
    fun login(@AuthenticationPrincipal user: UserDetails): UserDetails {
        log.info("Logged in admin user: [${user.username}]")
        return user
    }

    companion object {
        private val log = LoggerFactory.getLogger(AdminLoginController::class.java)
    }
}
