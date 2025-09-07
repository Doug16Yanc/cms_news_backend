package douglas.cms_news_backend.service

import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.model.Category
import douglas.cms_news_backend.model.Category.Companion.generateSlug
import douglas.cms_news_backend.model.Tag
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.repository.TagRepository
import douglas.cms_news_backend.service.validations.validateArticlePermissions
import douglas.cms_news_backend.service.validations.validateTagPermissions
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TagService(
    private val tagRepository: TagRepository,
) {

    fun createTag(tagName: String) : Tag {
        tagName.let {
            tagRepository.findByName(it)
                .orElseThrow { EntityNotFoundException("Tag já cadastrada.") }
        }

        val slug = generateSlug(tagName)

        val newTag = Tag(
            name = tagName,
            slug = slug,
            createdAt = LocalDateTime.now(),
        )

        return tagRepository.save(newTag)
    }

    fun findAllByNames(names: List<String>): List<Tag> {
        return tagRepository.findAllByName(names)
    }

    fun findAllTags(page: Int, size: Int, sort: String = "name"): Page<Tag> {
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(sort).ascending()
        )
        return tagRepository.findAll(pageable)
    }

    fun updateTag(tagName: String, currentUser: User): Tag? {
        val existingTag = tagName.let {
            tagRepository.findByName(it)
                .orElseThrow { EntityNotFoundException("Tag não encontrada.") }
        }

        validateTagPermissions(existingTag, currentUser, "editar")

        existingTag?.name = tagName

        val newSlug = generateSlug(tagName)
        existingTag?.slug = newSlug

        return existingTag?.let { tagRepository.save(it) }
    }

    fun deleteTag(tagName: String, currentUser: User) {
        val tag = tagRepository.findByName(tagName)
            .orElseThrow { EntityNotFoundException("Tag não encontrada.") }

        validateTagPermissions(tag, currentUser, "excluir")

        tagRepository.delete(tag)
    }
}