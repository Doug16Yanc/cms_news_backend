package douglas.cms_news_backend.exception.local

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.apply {
            status = HttpServletResponse.SC_UNAUTHORIZED
            contentType = "application/json"
            writer.write(createErrorJson(authException))
        }
    }

    private fun createErrorJson(authException: AuthenticationException): String {
        return """
        {
            "error": "Authentication is required to access this resource.",
            "message": "${authException.message}",
            "timestamp": "${LocalDateTime.now()}"
        }
        """.trimIndent()
    }
}