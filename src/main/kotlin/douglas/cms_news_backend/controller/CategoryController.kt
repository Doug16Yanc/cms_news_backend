package douglas.cms_news_backend.controller

import douglas.cms_news_backend.dto.CategoryDto
import douglas.cms_news_backend.dto.CreateOrUpdateCategoryDto
import douglas.cms_news_backend.model.Category
import douglas.cms_news_backend.service.CategoryService
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
    fun createTag(@PathVariable tagName : String, @RequestBody categoryDto: CreateOrUpdateCategoryDto): ResponseEntity<String> {
        val newTag = categoryService.createCategory(categoryDto, tagName)

        return ResponseEntity.status(HttpStatus.CREATED).body("Nova categoria : " + newTag.name + " criada com sucesso!")
    }

    @GetMapping("/find-all-categories")
    fun getAllTags(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort: String
    ): Page<CategoryDto> {
        val pageable = PageRequest.of(page, size, Sort.by(sort).ascending())
        return categoryService.findAllCategories(page, size, sort)
    }

    @PutMapping("/update-category/{id}")
    @PreAuthorize("hasAuthority('JORNALISTA') or hasAuthority('EDITOR')")
    fun updateCategory(
        @PathVariable id : String,
        @RequestBody categoryDto: CreateOrUpdateCategoryDto,
    ): ResponseEntity<CategoryDto> {
        val category = categoryService.updateCategory(id, categoryDto)
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