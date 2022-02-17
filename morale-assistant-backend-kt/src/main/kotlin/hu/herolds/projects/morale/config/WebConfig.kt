package hu.herolds.projects.morale.config

import hu.herolds.projects.morale.service.authentication.AdminUserDetailsService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
class WebConfig(
    private val corsParameters: CorsParameters
): WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        val mapping = registry.addMapping("/**")

        corsParameters.allowCredentials?.also {
            mapping.allowCredentials(it)
        }
        corsParameters.allowedHeaders.takeIf { it.isNotEmpty() }?.also {
            mapping.allowedHeaders(*corsParameters.allowedHeadersParams)
        }
        corsParameters.allowedOrigins.takeIf { it.isNotEmpty() }?.also {
            mapping.allowedOrigins(*corsParameters.allowedOriginsParams)
        }
        corsParameters.allowedMethods.takeIf { it.isNotEmpty() }?.also {
            mapping.allowedMethods(*corsParameters.allowedMethodsParams)
        }
    }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfigurer: WebSecurityConfigurerAdapter() {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Autowired
    fun configureAuth(authentication: AuthenticationManagerBuilder, adminUserDetailsService: AdminUserDetailsService) {
        authentication.userDetailsService(adminUserDetailsService)
    }

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                // Allow publicly accessible endpoint
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
            "/actuator/health",

            "/favicon.ico",

            "/api/public/**",

            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
        )
    }

}

class UnauthorizedEntryPoint : AuthenticationEntryPoint {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun commence(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authException: AuthenticationException?
    ) {
        log.error("Unauthorized access - Path: [${request.requestURI}], Params: [${request.queryString}],Message: [${authException?.localizedMessage}]", )
        response.sendError(HttpStatus.UNAUTHORIZED.value())
    }
}
