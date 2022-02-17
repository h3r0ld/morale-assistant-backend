package hu.herolds.projects.morale.controller.admin

import hu.herolds.projects.morale.controller.dto.ChangePasswordDto
import hu.herolds.projects.morale.service.admin.AdminUserService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/admin", produces = [APPLICATION_JSON_VALUE])
class AdminUserController(
    private val adminUserService: AdminUserService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/login")
    fun login(@AuthenticationPrincipal user: UserDetails): UserDetails {
        log.info("Logged in admin user: [${user.username}]")
        return user
    }

    @PostMapping("/change-password")
    fun changePassword(
        @AuthenticationPrincipal user: UserDetails,
        @RequestBody changePassword: ChangePasswordDto
    ) {
        log.info("Changing password for admin user: [${user.username}]")
        adminUserService.changePassword(user, changePassword)
    }
}
