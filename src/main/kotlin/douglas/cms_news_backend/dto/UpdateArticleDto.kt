package douglas.cms_news_backend.dto

import douglas.cms_news_backend.model.enums.ArticleStatus
import java.time.LocalDate

data class UpdateArticleDTO(
    val title: String? = null,
    val subtitle: String? = null,
    val content: String? = null,
    val coverImage: String? = null,
    val articleStatus: ArticleStatus? = null,
    val publishedDate: LocalDate? = null,
    val categoryName: String? = null,
    val tagNames: List<String>? = null
)