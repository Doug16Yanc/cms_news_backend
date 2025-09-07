package douglas.cms_news_backend.controller

import douglas.cms_news_backend.dto.UpdateArticleDTO
import douglas.cms_news_backend.model.Article
import douglas.cms_news_backend.model.Tag
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.TagService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tags")
class TagController(
    private val tagService: TagService
) {

    @PostMapping("/create-tag/{tagName}")
    @PreAuthorize("hasRole('JORNALIST') or hasRole('EDITOR')")
    fun createTag(@PathVariable tagName : String): ResponseEntity<String> {
        val newTag = tagService.createTag(tagName)

        return ResponseEntity.status(HttpStatus.CREATED).body("Nova tag : " + newTag.name + " criada com sucesso!")
    }

    @GetMapping("/find-all-tags")
    fun getAllTags(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort: String
    ): Page<Tag> {
        val pageable = PageRequest.of(page, size, Sort.by(sort).ascending())
        return tagService.findAllTags(page, size, sort)
    }

    @PutMapping("/update-tag/{tagName}")
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('EDITOR')")
    fun updateTag(
        @PathVariable tagName : String,
        authentication: Authentication
    ): ResponseEntity<Tag> {
        val currentUser = authentication.principal as User
        val tag = tagService.updateTag(tagName, currentUser)
        return ResponseEntity.ok(tag)
    }

    @DeleteMapping("delete-tag/{tagName}")
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('EDITOR')")
    fun deleteTag(
        @PathVariable tagName: String,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val currentUser = authentication.principal as User
        tagService.deleteTag(tagName, currentUser)
        return ResponseEntity.noContent().build()
    }
}