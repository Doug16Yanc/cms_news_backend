package douglas.cms_news_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "comments")
data class Comment(
    @Id
    val id: Object? = null,

    val content: String,

    val author: String,

    val authorId: String,

    val newsId: String,

    val parentCommentId: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    val updatedAt: LocalDateTime = LocalDateTime.now(),

    val likes: Int = 0,

    val dislikes: Int = 0,

    val isActive: Boolean = true
)

data class CommentRequest(
    val content: String,
    val parentCommentId: String? = null
)

data class CommentResponse(
    val id: String,
    val content: String,
    val author: String,
    val authorId: String,
    val newsId: String,
    val parentCommentId: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val likes: Int,
    val dislikes: Int,
    val replies: List<CommentResponse> = emptyList()
)
