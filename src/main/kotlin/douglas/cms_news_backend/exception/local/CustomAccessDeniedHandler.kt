package douglas.cms_news_backend.exception.local

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.apply {
            status = HttpServletResponse.SC_FORBIDDEN
            contentType = "application/json"
            writer.write(createErrorJson(accessDeniedException, request))
        }
    }

    private fun createErrorJson(accessDeniedException: AccessDeniedException, request: HttpServletRequest): String {
        return """
        {
            "error": "Access Denied",
            "message": "You are not authorized to perform this action.",
            "details": "${accessDeniedException.message}",
            "path": "${request.requestURI}",
            "timestamp": "${LocalDateTime.now()}"
        }
        """.trimIndent()
    }
}