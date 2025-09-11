package douglas.cms_news_backend.mapper

import douglas.cms_news_backend.dto.ArticleDto
import douglas.cms_news_backend.model.Article
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class ArticleMapper {
    fun mapArticleToDto(article: Article): ArticleDto {
        return ArticleDto(
            id = article.id,
            title = article.title,
            subtitle = article.subtitle,
            content = article.content,
            coverImage = article.coverImage,
            slug = article.slug,
            authorName = article.author.name,
            articleStatus = article.articleStatus,
            publishedDate = article.publishedDate,
            categoryName = article.category.name,
            tagNames = article.tags.map { it.name }
        )
    }
}