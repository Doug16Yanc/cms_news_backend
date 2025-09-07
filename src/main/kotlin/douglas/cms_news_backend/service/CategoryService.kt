package douglas.cms_news_backend.service

import douglas.cms_news_backend.exception.local.EntityAlreadyExistsException
import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.model.Category
import douglas.cms_news_backend.model.Category.Companion.generateSlug
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.repository.CategoryRepository
import douglas.cms_news_backend.service.extensions.hasJournalistOrEditorRole
import douglas.cms_news_backend.service.validations.validateCategoryPermissions
import douglas.cms_news_backend.service.validations.validateTagPermissions
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    fun createCategory(category: Category, currentUser : User) : Category {
        if (!currentUser.hasJournalistOrEditorRole()) {
            throw AccessDeniedException("Apenas jornalistas e editores podem criar categorias.")
        }

        category.name.let {
            categoryRepository.findByName(it)
                ?: EntityNotFoundException("Categoria já cadastrada.") }


        val authentication = SecurityContextHolder.getContext().authentication
        val user = authentication.principal as User
        if (!user.isEnabled()) throw IllegalStateException("Usuário desativado")

        val slug = generateSlug(category.name)

        val newCategory = Category(
            name = category.name,
            description = category.description,
            slug = category.slug,
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

    fun updateCategory(category: Category, currentUser: User): Category? {
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

    fun deleteCategory(categoryName: String, currentUser: User) {
        val category = categoryRepository.findByName(categoryName)
            ?: throw EntityNotFoundException("Categoria não encontrada.")

        validateCategoryPermissions(category, currentUser, "excluir")

        categoryRepository.delete(category)
    }
}