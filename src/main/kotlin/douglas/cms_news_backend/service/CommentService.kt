package douglas.cms_news_backend.service

import douglas.cms_news_backend.dto.PageCommentsResponseDto
import douglas.cms_news_backend.model.Comment
import douglas.cms_news_backend.model.CommentRequest
import douglas.cms_news_backend.model.CommentResponse
import douglas.cms_news_backend.repository.CommentRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository
) {

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
        const val MAX_PAGE_SIZE = 50
    }

    fun createComment(
        request: CommentRequest,
        newsId: String,
        author: String,
        authorId: String
    ): Comment {
        val comment = Comment(
            content = request.content,
            author = author,
            authorId = authorId,
            newsId = newsId,
            parentCommentId = request.parentCommentId
        )

        return commentRepository.save(comment)
    }

    fun getCommentsByNewsId(
        newsId: String,
        page: Int = 0,
        size: Int = DEFAULT_PAGE_SIZE,
        sortBy: String = "createdAt",
        sortDirection: String = "desc"
    ): PageCommentsResponseDto {
        val pageSize = size.coerceAtMost(MAX_PAGE_SIZE)
        val sort = Sort.by(
            if (sortDirection.equals("asc", ignoreCase = true))
                Sort.Direction.ASC
            else
                Sort.Direction.DESC,
            sortBy
        )
        val pageable: Pageable = PageRequest.of(page, pageSize, sort)

        val commentsPage = commentRepository.findByNewsIdAndParentCommentIdIsNullAndIsActiveTrue(
            newsId,
            pageable
        )

        val commentResponses = commentsPage.content.map { comment ->
            val replies = getReplies(comment.id!!.toString(), 0, 20)
            mapToResponse(comment, replies)
        }

        return PageCommentsResponseDto(
            content = commentResponses,
            currentPage = commentsPage.number,
            totalPages = commentsPage.totalPages,
            totalItems = commentsPage.totalElements,
            pageSize = commentsPage.size,
            hasNext = commentsPage.hasNext(),
            hasPrevious = commentsPage.hasPrevious()
        )
    }

    fun getCommentReplies(
        parentCommentId: String,
        page: Int = 0,
        size: Int = DEFAULT_PAGE_SIZE
    ): PageCommentsResponseDto {
        val pageSize = size.coerceAtMost(MAX_PAGE_SIZE)
        val pageable: Pageable = PageRequest.of(
            page,
            pageSize,
            Sort.by(Sort.Direction.ASC, "createdAt")
        )

        val repliesPage = commentRepository.findByParentCommentIdAndIsActiveTrueOrderByCreatedAtAsc(
            parentCommentId,
            pageable
        )

        val replyResponses = repliesPage.content.map { reply ->
            val nestedReplies = getReplies(reply.id!!.toString(), 0, 10)
            mapToResponse(reply, nestedReplies)
        }

        return PageCommentsResponseDto(
            content = replyResponses,
            currentPage = repliesPage.number,
            totalPages = repliesPage.totalPages,
            totalItems = repliesPage.totalElements,
            pageSize = repliesPage.size,
            hasNext = repliesPage.hasNext(),
            hasPrevious = repliesPage.hasPrevious()
        )
    }

    fun getCommentById(id: String): Comment? {
        return commentRepository.findCommentById(id).orElse(null)
    }

    fun updateComment(id: String, content: String, authorId: String): Comment? {
        val comment = commentRepository.findCommentById(id).orElse(null)

        if (comment != null && comment.authorId == authorId) {
            val updatedComment = comment.copy(
                content = content,
                updatedAt = java.time.LocalDateTime.now()
            )
            return commentRepository.save(updatedComment)
        }

        return null
    }

    @Transactional
    fun deleteComment(id: String, authorId: String): Boolean {
        val comment = commentRepository.findCommentById(id).orElse(null)

        if (comment != null && comment.authorId == authorId) {
            val deletedComment = comment.copy(isActive = false)
            commentRepository.save(deletedComment)
            return true
        }

        return false
    }

    fun likeComment(id: String): Comment? {
        val comment = commentRepository.findCommentById(id).orElse(null)

        return comment?.let {
            val updatedComment = it.copy(likes = it.likes + 1)
            commentRepository.save(updatedComment)
        }
    }

    fun dislikeComment(id: String): Comment? {
        val comment = commentRepository.findCommentById(id).orElse(null)

        return comment?.let {
            val updatedComment = it.copy(dislikes = it.dislikes + 1)
            commentRepository.save(updatedComment)
        }
    }

    fun getCommentsCount(newsId: String): Long {
        return commentRepository.countByNewsIdAndIsActiveTrue(newsId)
    }

    fun getRepliesCount(parentCommentId: String): Long {
        return commentRepository.countByParentCommentIdAndIsActiveTrue(parentCommentId)
    }

    fun getUserComments(
        authorId: String,
        page: Int = 0,
        size: Int = DEFAULT_PAGE_SIZE
    ): PageCommentsResponseDto {
        val pageSize = size.coerceAtMost(MAX_PAGE_SIZE)
        val pageable: Pageable = PageRequest.of(
            page,
            pageSize,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )

        val userCommentsPage = commentRepository.findByAuthorIdAndIsActiveTrueOrderByCreatedAtDesc(
            authorId,
            pageable
        )

        val commentResponses = userCommentsPage.content.map { comment ->
            val replies = getReplies(comment.id!!.toString(), 0, 5)
            mapToResponse(comment, replies)
        }

        return PageCommentsResponseDto(
            content = commentResponses,
            currentPage = userCommentsPage.number,
            totalPages = userCommentsPage.totalPages,
            totalItems = userCommentsPage.totalElements,
            pageSize = userCommentsPage.size,
            hasNext = userCommentsPage.hasNext(),
            hasPrevious = userCommentsPage.hasPrevious()
        )
    }

    private fun getReplies(parentId: String, page: Int, size: Int): List<CommentResponse> {
        val pageSize = size.coerceAtMost(MAX_PAGE_SIZE)
        val pageable: Pageable = PageRequest.of(
            page,
            pageSize,
            Sort.by(Sort.Direction.ASC, "createdAt")
        )

        val repliesPage = commentRepository.findByParentCommentIdAndIsActiveTrueOrderByCreatedAtAsc(
            parentId,
            pageable
        )

        return repliesPage.content.map { reply ->
            val nestedReplies = if (page == 0) {
                getReplies(reply.id!!.toString(), 0, 5)
            } else {
                emptyList()
            }
            mapToResponse(reply, nestedReplies)
        }
    }

    private fun mapToResponse(comment: Comment, replies: List<CommentResponse> = emptyList()): CommentResponse {
        return CommentResponse(
            id = comment.id!!.toString(),
            content = comment.content,
            author = comment.author,
            authorId = comment.authorId,
            newsId = comment.newsId,
            parentCommentId = comment.parentCommentId,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt,
            likes = comment.likes,
            dislikes = comment.dislikes,
            replies = replies
        )
    }
}