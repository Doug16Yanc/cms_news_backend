package douglas.cms_news_backend.repository

import douglas.cms_news_backend.model.Comment
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommentRepository : MongoRepository<Comment, ObjectId> {

    fun findByNewsIdAndParentCommentIdIsNullAndIsActiveTrue(
        newsId: String,
        pageable: Pageable
    ): Page<Comment>

    fun findByParentCommentIdAndIsActiveTrueOrderByCreatedAtAsc(
        parentCommentId: String,
        pageable: Pageable
    ): Page<Comment>

    fun findByNewsIdAndIsActiveTrue(
        newsId: String,
        pageable: Pageable
    ): Page<Comment>

    fun findCommentById(id: String): Optional<Comment>

    fun countByNewsIdAndIsActiveTrue(newsId: String): Long

    fun countByParentCommentIdAndIsActiveTrue(parentCommentId: String): Long

    fun findByAuthorIdAndIsActiveTrueOrderByCreatedAtDesc(
        authorId: String,
        pageable: Pageable
    ): Page<Comment>

    @Query("{'newsId': ?0, 'isActive': true}")
    fun findActiveCommentsByNewsId(newsId: String, pageable: Pageable): Page<Comment>

    fun findByParentCommentIdAndIsActiveTrueOrderByCreatedAtAsc(parentCommentId: String): List<Comment>
}