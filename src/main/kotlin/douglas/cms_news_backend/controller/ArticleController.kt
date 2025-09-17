package douglas.cms_news_backend.controller

import douglas.cms_news_backend.dto.ArticleDto
import douglas.cms_news_backend.dto.CreateArticleDTO
import douglas.cms_news_backend.dto.UpdateArticleDTO
import douglas.cms_news_backend.model.Article
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.ArticleService
import douglas.cms_news_backend.utils.AuthUtil
import jakarta.validation.Valid
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/articles")
class ArticleController(
    private val articleService: ArticleService,
) {

    @PostMapping("/create-article", consumes = ["multipart/form-data"])
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun createArticle(
        @RequestPart("article") @Valid dto: CreateArticleDTO,
        @RequestPart(value = "picture", required = false) picture: MultipartFile
    ): ResponseEntity<ArticleDto> {
        val article = articleService.createArticle(dto, picture)
        return ResponseEntity.status(HttpStatus.CREATED).body(article)
    }

    @PutMapping("/update-article/{id}")
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun updateArticle(
        @PathVariable id: String,
        @RequestBody @Valid dto: UpdateArticleDTO,
        @RequestPart(value = "picture", required = false) picture: MultipartFile
    ): ResponseEntity<ArticleDto> {
        val article = articleService.updateArticle(id, dto, picture)
        return ResponseEntity.ok(article)
    }

    @DeleteMapping("delete-article/{id}")
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun deleteArticle(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        articleService.deleteArticle(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/search-by-author/{authorId}")
    fun getUserArticles(
        @PathVariable authorId: String,
        @PageableDefault(size = 10, sort = ["publishedDate"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<ArticleDto> {
        val objectId = ObjectId(authorId)
        return articleService.getUserArticles(objectId, pageable)
    }

    @GetMapping("/search")
    fun searchPublishedArticles(
        @RequestParam searchTerm: String,
        @PageableDefault(size = 10, sort = ["publishedDate"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<ArticleDto> {
        val now = LocalDateTime.now()
        return articleService.searchPublishedArticles(now, searchTerm, pageable)
    }

    @GetMapping("/get-all-published")
    fun getPublishedArticles(
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) tags: List<String>?,
        @PageableDefault(size = 10, sort = ["publishedDate"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<ArticleDto> {
        return articleService.getPublishedArticles(pageable, category, tags)
    }

    @GetMapping("get-by-slug/{slug}")
    fun getArticleBySlug(@PathVariable slug: String): ResponseEntity<ArticleDto> {
        val article = articleService.getArticleBySlug(slug)
        return if (article != null) ResponseEntity.ok(article)
        else ResponseEntity.notFound().build()
    }

    @GetMapping("/get-by-category/{category}")
    fun getArticlesByCategory(
        @PathVariable category: String,
        @PageableDefault(size = 10, sort = ["publishedDate"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<ArticleDto> {
        return articleService.getArticlesByCategoryName(category, pageable)
    }

    @PostMapping("/increment-view-count/{articleId}/views")
    fun incrementViewCount(@PathVariable articleId: String): ResponseEntity<Void> {
        articleService.incrementViewCount(ObjectId(articleId))
        return ResponseEntity.ok().build()
    }
}