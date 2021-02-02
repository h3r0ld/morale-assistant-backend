package hu.herolds.projects.morale.config

import hu.herolds.projects.morale.service.authentication.AdminUserDetailsService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class WebConfig: WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
    }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfigurer: WebSecurityConfigurerAdapter() {

    @Autowired
    fun configureAuth(authentication: AuthenticationManagerBuilder, adminUserDetailsService: AdminUserDetailsService) {
        authentication
                .userDetailsService(adminUserDetailsService)
                // TODO: !!!
                //.passwordEncoder(NoOpPasswordEncoder.getInstance())
    }

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                // Allow publicly accessable endpoint
                .antMatchers(*PUBLIC_URLS).permitAll()
                // Any other endpoints are authenticated
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                // If authentication fails, return 401 Unauthorized status.
                .exceptionHandling().authenticationEntryPoint(UnauthorizedEntryPoint())
    }

    companion object {
        private val PUBLIC_URLS = arrayOf(
                "/morale-boost/**"
        )
    }
}

class UnauthorizedEntryPoint : AuthenticationEntryPoint {
    override fun commence(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authException: AuthenticationException?
    ) {
        log.error("Unauthorized access.", authException)
        response.sendError(HttpStatus.UNAUTHORIZED.value())
    }

    companion object {
        private val log = LoggerFactory.getLogger(UnauthorizedEntryPoint::class.java)
    }
}
