package douglas.cms_news_backend.dto

import douglas.cms_news_backend.model.enums.ArticleStatus
import org.bson.types.ObjectId
import java.time.LocalDate

data class ArticleDto(
    val id: ObjectId? = null,

    val title: String,

    val subtitle: String,

    val content: String,

    val coverImage: String,

    val slug: String,

    val authorName : String,

    val articleStatus: ArticleStatus? = null,

    val publishedDate: LocalDate? = null,

    val categoryName: String,

    val tagNames: List<String>
)
