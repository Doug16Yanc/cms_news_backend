package douglas.cms_news_backend.controller


import douglas.cms_news_backend.model.CommentRequest
import douglas.cms_news_backend.service.CommentService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments/{newsId}")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping
    fun createComment(
        @PathVariable newsId: String,
        @Valid @RequestBody request: CommentRequest,
        @RequestHeader("X-User-Id") userId: String,
        @RequestHeader("X-User-Name") userName: String
    ): ResponseEntity<Any> {
        val comment = commentService.createComment(request, newsId, userName, userId)
        return ResponseEntity.ok(comment)
    }

    @GetMapping
    fun getComments(
        @PathVariable newsId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDirection: String
    ): ResponseEntity<Any> {
        val comments = commentService.getCommentsByNewsId(newsId, page, size, sortBy, sortDirection)
        return ResponseEntity.ok(comments)
    }

    @GetMapping("/{commentId}/replies")
    fun getCommentReplies(
        @PathVariable newsId: String,
        @PathVariable commentId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Any> {
        val replies = commentService.getCommentReplies(commentId, page, size)
        return ResponseEntity.ok(replies)
    }

    @GetMapping("/count")
    fun getCommentsCount(@PathVariable newsId: String): ResponseEntity<Map<String, Long>> {
        val count = commentService.getCommentsCount(newsId)
        return ResponseEntity.ok(mapOf("count" to count))
    }

    @GetMapping("/{commentId}/replies/count")
    fun getRepliesCount(
        @PathVariable newsId: String,
        @PathVariable commentId: String
    ): ResponseEntity<Map<String, Long>> {
        val count = commentService.getRepliesCount(commentId)
        return ResponseEntity.ok(mapOf("count" to count))
    }

    @PutMapping("/{commentId}")
    fun updateComment(
        @PathVariable newsId: String,
        @PathVariable commentId: String,
        @RequestBody content: Map<String, String>,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<Any> {
        val updatedComment = commentService.updateComment(commentId, content["content"] ?: "", userId)

        return if (updatedComment != null) {
            ResponseEntity.ok(updatedComment)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable newsId: String,
        @PathVariable commentId: String,
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<Any> {
        val deleted = commentService.deleteComment(commentId, userId)

        return if (deleted) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{commentId}/like")
    fun likeComment(
        @PathVariable newsId: String,
        @PathVariable commentId: String
    ): ResponseEntity<Any> {
        val comment = commentService.likeComment(commentId)

        return if (comment != null) {
            ResponseEntity.ok(comment)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{commentId}/dislike")
    fun dislikeComment(
        @PathVariable newsId: String,
        @PathVariable commentId: String
    ): ResponseEntity<Any> {
        val comment = commentService.dislikeComment(commentId)

        return if (comment != null) {
            ResponseEntity.ok(comment)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/user/{userId}")
    fun getUserComments(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Any> {
        val comments = commentService.getUserComments(userId, page, size)
        return ResponseEntity.ok(comments)
    }
}