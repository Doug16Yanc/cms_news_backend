package douglas.cms_news_backend.dto

import douglas.cms_news_backend.model.CommentResponse

data class PageCommentsResponseDto(
    val content: List<CommentResponse>,
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Long,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)