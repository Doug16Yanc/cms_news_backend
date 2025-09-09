package douglas.cms_news_backend.controller

import douglas.cms_news_backend.model.Category
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.CategoryService
import douglas.cms_news_backend.utils.AuthUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("category")
class CategoryController(
    private val categoryService: CategoryService,
) {
    @PostMapping("/create-category/{tagName}")
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun createTag(@PathVariable tagName : String, @RequestBody category: Category): ResponseEntity<String> {
        val newTag = categoryService.createCategory(category, tagName)

        return ResponseEntity.status(HttpStatus.CREATED).body("Nova categoria : " + newTag.name + " criada com sucesso!")
    }

    @GetMapping("/find-all-categories")
    fun getAllTags(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort: String
    ): Page<Category> {
        val pageable = PageRequest.of(page, size, Sort.by(sort).ascending())
        return categoryService.findAllCategories(page, size, sort)
    }

    @PutMapping("/update-category/{tagName}")
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun updateCategory(
        @RequestBody category: Category,
    ): ResponseEntity<Category> {
        val category = categoryService.updateCategory(category)
        return ResponseEntity.ok(category)
    }

    @DeleteMapping("delete-category/{categoryName}")
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun deleteCategory(
        @PathVariable categoryName: String,
    ): ResponseEntity<Void> {
        categoryService.deleteCategory(categoryName)
        return ResponseEntity.noContent().build()
    }
}