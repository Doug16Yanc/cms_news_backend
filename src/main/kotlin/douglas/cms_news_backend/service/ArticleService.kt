package douglas.cms_news_backend.service

import douglas.cms_news_backend.dto.ArticleDto
import douglas.cms_news_backend.dto.CreateArticleDTO
import douglas.cms_news_backend.dto.UpdateArticleDTO
import douglas.cms_news_backend.exception.local.BadRequestException
import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.mapper.ArticleMapper
import douglas.cms_news_backend.model.Article
import douglas.cms_news_backend.model.Tag
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.model.enums.ArticleStatus
import douglas.cms_news_backend.repository.ArticleRepository
import douglas.cms_news_backend.service.extensions.hasJournalistOrEditorRole
import douglas.cms_news_backend.service.validations.validateArticlePermissions
import douglas.cms_news_backend.utils.AuthUtil
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import java.util.regex.Pattern

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val tagService: TagService,
    private val mongoTemplate: MongoTemplate,
    private val categoryService: CategoryService,
    private val authUtil: AuthUtil,
    private val articleMapper: ArticleMapper
) {

    fun generateSlug(word: String): String {
        return word.lowercase()
            .replace("[^a-z0-9\\s-]".toRegex(), "")
            .replace("\\s+".toRegex(), "-")
            .replace("-+".toRegex(), "-")
            .plus("-${UUID.randomUUID().toString().substring(0, 8)}")
    }

    fun getPublishedArticles(
        pageable: Pageable,
        category: String?,
        tags: List<String>?
    ): Page<ArticleDto> {
        val now = LocalDateTime.now()
        val articles: Page<Article> = when {
            category != null -> {
                val categoryId = try {
                    ObjectId(category)
                } catch (e: IllegalArgumentException) {
                    return PageImpl(emptyList(), pageable, 0)
                }
                articleRepository.findByCategory(now, categoryId, pageable)
            }
            !tags.isNullOrEmpty() -> {
                val tagIds = tags.mapNotNull { try { ObjectId(it) } catch (e: IllegalArgumentException) { null } }
                if (tagIds.isEmpty()) return PageImpl(emptyList(), pageable, 0)
                else articleRepository.findByTags(now, tagIds, pageable)
            }
            else -> articleRepository.findPublishedArticles(now, pageable)
        }

        val articleDtos = articles.content.map { article ->
            articleMapper.mapArticleToDto(article)
        }

        return PageImpl(articleDtos, pageable, articles.totalElements)
    }

        fun getArticleBySlug(slug: String): ArticleDto? {
        val article = articleRepository.findBySlug(slug).orElse(null)

        return articleMapper.mapArticleToDto(article)
    }

    @Transactional
    fun incrementViewCount(articleId: ObjectId) {
        val article = articleRepository.findById(articleId).orElse(null)
        article?.let {
            val updated = it.copy(viewCount = it.viewCount + 1)
            articleRepository.save(updated)
        }
    }

    fun createArticle(dto: CreateArticleDTO): ArticleDto {
        val currentUser = authUtil.getCurrentUser()

        if (!currentUser.hasJournalistOrEditorRole()) {
            throw AccessDeniedException("Apenas jornalistas e editores podem criar artigos.")
        }

        val category = categoryService.findByName(dto.categoryName)
            ?: throw BadRequestException("Categoria '${dto.categoryName}' não encontrada.")

        val validTags = mutableListOf<Tag>()
        for (tagName in dto.tagNames) {
            val trimmedName = tagName.trim()
            val tag = tagService.findTagByName(trimmedName)
            if (tag != null) validTags.add(tag)
        }

        if (validTags.isEmpty()) {
            throw BadRequestException("Pelo menos uma tag válida é obrigatória.")
        }

        val slug = generateSlug(dto.title)

        val article = Article(
            title = dto.title,
            subtitle = dto.subtitle,
            content = dto.content,
            coverImage = dto.coverImage,
            slug = slug,
            articleStatus = dto.articleStatus ?: ArticleStatus.DRAFT,
            publishedDate = dto.publishedDate ?: LocalDate.now(),
            author = currentUser,
            category = category,
            tags = validTags.toMutableList(),
            viewCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedArticle = articleRepository.save(article)

        return articleMapper.mapArticleToDto(savedArticle)
    }

    fun updateArticle(articleId: String, dto: UpdateArticleDTO): ArticleDto {
        val currentUser = authUtil.getCurrentUser()

        val article = articleRepository.findById(ObjectId(articleId))
            .orElseThrow { EntityNotFoundException("Artigo não encontrado.") }

        validateArticlePermissions(article, currentUser, "editar")

        val updatedArticle = article.copy(
            title = dto.title ?: article.title,
            subtitle = dto.subtitle ?: article.subtitle,
            content = dto.content ?: article.content,
            coverImage = dto.coverImage ?: article.coverImage,
            articleStatus = dto.articleStatus ?: article.articleStatus,
            publishedDate = dto.publishedDate ?: article.publishedDate,
            updatedAt = LocalDateTime.now()
        )

        if (dto.categoryName != null) {
            val categoryName = try {
                dto.categoryName
            } catch (e : BadRequestException) {
                throw BadRequestException("Nome de categoria inválido")
            }
            val category = categoryService.findByName(categoryName)
            if (category != null) {
                updatedArticle.category = category
            }
        }

        if (!dto.tagNames.isNullOrEmpty()) {
            val tagNames = dto.tagNames.mapNotNull { try { it } catch (e: IllegalArgumentException) { null } }
            if (tagNames.isNotEmpty()) {
                val tags = tagService.findAllByNames(tagNames)
                if (tags.size != tagNames.size) throw EntityNotFoundException("Uma ou mais tags não encontradas.")
                updatedArticle.tags = tags
            }
        }

        val upatedArticle = articleRepository.save(updatedArticle)

        return articleMapper.mapArticleToDto(upatedArticle)
    }

    fun deleteArticle(articleId: String) {
        val currentUser = authUtil.getCurrentUser()
        val article = articleRepository.findById(ObjectId(articleId))
            .orElseThrow { EntityNotFoundException("Artigo não encontrado") }

        validateArticlePermissions(article, currentUser, "excluir")

        articleRepository.deleteById(ObjectId(articleId))
    }

    @Scheduled(fixedRate = 300000)
    fun publishScheduledArticles() {
        val now = LocalDateTime.now()
        val scheduledArticles = articleRepository.findScheduledArticlesToPublish(now)
        scheduledArticles.forEach { article ->
            val publishedArticle = article.copy(articleStatus = ArticleStatus.PUBLISHED)
            articleRepository.save(publishedArticle)
        }
    }

    fun getUserArticles(authorId: ObjectId, pageable: Pageable): Page<ArticleDto> {
        val articles = articleRepository.findByAuthorId(authorId, pageable)

        return articles.map { article ->
            articleMapper.mapArticleToDto(article)
        }
    }

    fun searchPublishedArticles(
        currentDate: LocalDateTime,
        searchTerm: String,
        pageable: Pageable
    ): Page<ArticleDto> {
        if (searchTerm.isBlank()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        val escapedSearchTerm = Pattern.quote(searchTerm)

        val query = Query(
            Criteria.where("articleStatus").`is`(ArticleStatus.PUBLISHED)
                .and("publishedDate").lte(currentDate)
                .orOperator(
                    Criteria.where("title").regex(escapedSearchTerm, "i"),
                    Criteria.where("content").regex(escapedSearchTerm, "i"),
                    Criteria.where("subtitle").regex(escapedSearchTerm, "i")
                )
        )

        val total = mongoTemplate.count(query, Article::class.java)
        val articles = mongoTemplate.find(query.with(pageable), Article::class.java)

        val articleDtos = articles.map { article ->
            articleMapper.mapArticleToDto(article)
        }

        return PageImpl(articleDtos, pageable, total)
    }

}