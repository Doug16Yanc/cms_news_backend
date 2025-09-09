package douglas.cms_news_backend.controller

import douglas.cms_news_backend.dto.TagDto
import douglas.cms_news_backend.model.Tag
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.TagService
import douglas.cms_news_backend.utils.AuthUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tags")
class TagController(
    private val tagService: TagService,
    private val authUtil: AuthUtil
) {

    @PostMapping("/create-tag/{tagName}")
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun createTag(@PathVariable tagName : String): ResponseEntity<String> {
        val newTag = tagService.createTag(tagName)

        return ResponseEntity.status(HttpStatus.CREATED).body("Nova tag : " + newTag.name + " criada com sucesso!")
    }

    @GetMapping("/find-all-tags")
    fun getAllTags(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort: String
    ): Page<TagDto> {
        val pageable = PageRequest.of(page, size, Sort.by(sort).ascending())
        return tagService.findAllTags(page, size, sort)
    }

    @PutMapping("/update-tag/{tagName}")
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun updateTag(
        @PathVariable tagName : String): ResponseEntity<String> {
        val tag = tagService.updateTag(tagName)
        return ResponseEntity.ok().body("Tag " + tag?.name + " atualizada com sucesso.")
    }

    @DeleteMapping("delete-tag/{tagName}")
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun deleteTag(
        @PathVariable tagName: String
    ): ResponseEntity<Void> {
        tagService.deleteTag(tagName)
        return ResponseEntity.noContent().build()
    }
}