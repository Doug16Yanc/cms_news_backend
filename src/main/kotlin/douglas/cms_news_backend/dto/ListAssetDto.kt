package douglas.cms_news_backend.dto

class AssetPageDto(
    content: List<AssetDto>?,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val pageSize: Int
) {
    val content: List<AssetDto> = content ?: emptyList()
}
