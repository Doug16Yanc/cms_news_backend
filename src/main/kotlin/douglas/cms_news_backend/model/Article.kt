package douglas.cms_news_backend.model

import douglas.cms_news_backend.model.enums.ArticleStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document(collection = "articles")
data class Article(
    @Id
    val id : ObjectId? = null,

    val title : String,

    val slug : String,

    val subtitle : String,

    val content : String,

    val coverImage : String,

    val articleStatus : ArticleStatus,

    val publishedDate : LocalDate,

    @DBRef
    val author: User,

    @DBRef
    var category : Category,

    var tags : List<Tag>,

    val viewCount : Int,

    val createdAt : LocalDateTime,

    val updatedAt : LocalDateTime
)