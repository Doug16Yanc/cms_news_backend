package douglas.cms_news_backend.controller

import douglas.cms_news_backend.model.Category
import douglas.cms_news_backend.model.Tag
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.CategoryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("category")
class CategoryController(
    private val categoryService: CategoryService
) {
    @PostMapping("/create-category/{tagName}")
    @PreAuthorize("hasRole('JORNALIST') or hasRole('EDITOR')")
    fun createTag(@RequestBody category: Category, authentication: Authentication): ResponseEntity<String> {
        val currentUser = authentication.principal as User
        val newTag = categoryService.createCategory(category, currentUser)

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
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('EDITOR')")
    fun updateCategory(
        @RequestBody category: Category,
        authentication: Authentication
    ): ResponseEntity<Category> {
        val currentUser = authentication.principal as User
        val category = categoryService.updateCategory(category, currentUser)
        return ResponseEntity.ok(category)
    }

    @DeleteMapping("delete-category/{categoryName}")
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('EDITOR')")
    fun deleteCategory(
        @PathVariable categoryName: String,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val currentUser = authentication.principal as User
        categoryService.deleteCategory(categoryName, currentUser)
        return ResponseEntity.noContent().build()
    }
}