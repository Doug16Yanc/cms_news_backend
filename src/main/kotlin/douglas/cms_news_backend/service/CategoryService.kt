package douglas.cms_news_backend.service

import douglas.cms_news_backend.dto.CategoryDto
import douglas.cms_news_backend.dto.CreateOrUpdateCategoryDto
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

    fun createCategory(createCategoryDto: CreateOrUpdateCategoryDto, tagName : String) : Category {
        val currentUser = authUtil.getCurrentUser()
        if (!currentUser.hasJournalistOrEditorRole()) {
            throw AccessDeniedException("Apenas jornalistas e editores podem criar categorias.")
        }

        val existentTag = tagService.findTagByName(tagName)

        if (categoryRepository.findByName(createCategoryDto.name) != null) {
            throw IllegalArgumentException("Categoria já cadastrada.")
        }

        val slug = generateSlug(createCategoryDto.name)

        val newCategory = Category(
            name = createCategoryDto.name,
            description = createCategoryDto.description,
            slug = slug,
            author = currentUser,
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

    fun findAllCategories(page: Int, size: Int, sort: String = "name"): Page<CategoryDto> {
        val pageable = PageRequest.of(page, size, Sort.by(sort).ascending())

        val categories: Page<Category> = categoryRepository.findAll(pageable)

        return categories.map { category ->
            category.id?.let {
                category.description?.let { it1 ->
                    category.author.name.let { it2 ->
                        CategoryDto(
                            id = it.toString(),
                            name = category.name,
                            description = it1,
                            authorName = it2
                        )
                    }
                }
            }
        }
    }

    fun updateCategory(id: String, categoryDto: CreateOrUpdateCategoryDto): CategoryDto? {
        val currentUser = authUtil.getCurrentUser()

        val existingCategory = categoryRepository.findCategoryById(id)
            ?: throw EntityNotFoundException("Categoria não encontrada.")

        validateCategoryPermissions(existingCategory, currentUser, "editar")

        existingCategory.name = categoryDto.name
        existingCategory.description = categoryDto.description
        existingCategory.slug = generateSlug(categoryDto.name)
        existingCategory.updatedAt = LocalDateTime.now()

        val savedCategory = categoryRepository.save(existingCategory)

        return savedCategory.description?.let {
            CategoryDto(
                savedCategory.id.toString(),
                savedCategory.name,
                it,
                savedCategory.author.name,
            )
        }
    }

    fun deleteCategory(categoryName: String) {
        val currentUser = authUtil.getCurrentUser()

        val category = categoryRepository.findByName(categoryName)
            ?: throw EntityNotFoundException("Categoria não encontrada.")

        validateCategoryPermissions(category, currentUser, "excluir")

        categoryRepository.delete(category)
    }
}