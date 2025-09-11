package douglas.cms_news_backend.dto

import douglas.cms_news_backend.model.enums.ArticleStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateArticleDTO(
    val title: String,

    val subtitle: String,

    val content: String,

    val articleStatus: ArticleStatus? = null,

    val publishedDate: LocalDate? = null,

    val categoryName: String,

    val tagNames: List<String>
)