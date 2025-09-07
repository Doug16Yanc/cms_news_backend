package douglas.cms_news_backend.repository

import douglas.cms_news_backend.model.Article
import douglas.cms_news_backend.model.enums.ArticleStatus


import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface ArticleRepository : MongoRepository<Article, ObjectId> {

    fun findBySlug(slug: String): Optional<Article>

    fun findByArticleStatus(status: ArticleStatus, pageable: Pageable): Page<Article>

    fun findByAuthorId(authorId: ObjectId, pageable: Pageable): Page<Article>

    @Query("{ 'category.\$id': ?0 }")
    fun findByCategoryId(categoryId: ObjectId, pageable: Pageable): Page<Article>

    @Query("{ 'tags.\$id': ?0 }")
    fun findByTagId(tagId: ObjectId, pageable: Pageable): Page<Article>

    @Query("{ 'articleStatus': 'PUBLISHED', 'publishedDate': { '\$lte': ?0 } }")
    fun findPublishedArticles(currentDate: LocalDateTime, pageable: Pageable): Page<Article>

    @Query("{ 'articleStatus': 'SCHEDULED', 'publishedDate': { '\$lte': ?0 } }")
    fun findScheduledArticlesToPublish(currentDate: LocalDateTime): List<Article>

    @Query("{ 'articleStatus': 'PUBLISHED', 'publishedDate': { '\$lte': ?0 }, 'category.\$id': ?1 }")
    fun findByCategory(currentDate: LocalDateTime, categoryId: ObjectId, pageable: Pageable): Page<Article>

    @Query("{ 'articleStatus': 'PUBLISHED', 'publishedDate': { '\$lte': ?0 }, 'tags.\$id': { '\$in': ?1 } }")
    fun findByTags(currentDate: LocalDateTime, tagIds: List<ObjectId>, pageable: Pageable): Page<Article>
}