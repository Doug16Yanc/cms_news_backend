package douglas.cms_news_backend.service

import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.model.Category
import douglas.cms_news_backend.model.Category.Companion.generateSlug
import douglas.cms_news_backend.repository.CategoryRepository
import douglas.cms_news_backend.service.extensions.hasJournalistOrEditorRole
import douglas.cms_news_backend.service.validations.validateCategoryPermissions
import douglas.cms_news_backend.utils.AuthUtil
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val authUtil: AuthUtil,
    private val tagService: TagService
) {

    fun createCategory(category: Category, tagName : String) : Category {
        val currentUser = authUtil.getCurrentUser()
        if (!currentUser.hasJournalistOrEditorRole()) {
            throw AccessDeniedException("Apenas jornalistas e editores podem criar categorias.")
        }

        val existentTag = tagService.findTagByName(tagName)

        if (categoryRepository.findByName(category.name) != null) {
            throw IllegalArgumentException("Categoria já cadastrada.")
        }

        val slug = generateSlug(category.name)

        val newCategory = Category(
            name = category.name,
            description = category.description,
            slug = slug,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        return categoryRepository.save(newCategory)
    }

    fun findById(id: ObjectId): Optional<Category> {
        return categoryRepository.findById(id) ?: Optional.empty()
    }

    fun findByName(name: String): Category? {
        return categoryRepository.findByName(name) ?: throw EntityNotFoundException("Categoria não encontrada.")
    }

    fun findAllCategories(page: Int, size: Int, sort: String = "name"): Page<Category> {
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(sort).ascending()
        )
        return categoryRepository.findAll(pageable)
    }

    fun updateCategory(category: Category): Category? {
        val currentUser = authUtil.getCurrentUser()

        val existingCategory = category.id?.let {
            categoryRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Categoria não encontrada.") }
        }

        validateCategoryPermissions(category, currentUser, "editar")

        existingCategory?.name = category.name
        existingCategory?.description = category.description
        existingCategory?.slug = category.slug
        existingCategory?.updatedAt = LocalDateTime.now()

        return existingCategory?.let { categoryRepository.save(it) }
    }

    fun deleteCategory(categoryName: String) {
        val currentUser = authUtil.getCurrentUser()

        val category = categoryRepository.findByName(categoryName)
            ?: throw EntityNotFoundException("Categoria não encontrada.")

        validateCategoryPermissions(category, currentUser, "excluir")

        categoryRepository.delete(category)
    }
}