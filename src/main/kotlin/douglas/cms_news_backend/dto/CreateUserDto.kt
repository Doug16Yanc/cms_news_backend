package douglas.cms_news_backend.dto

import douglas.cms_news_backend.model.Role
import douglas.cms_news_backend.model.enums.ArticleStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateUserDto(
    val name: String,

    val email: String,

    val password: String,

    val role: String
)
