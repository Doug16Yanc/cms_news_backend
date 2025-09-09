package douglas.cms_news_backend.service

import douglas.cms_news_backend.dto.TagDto
import douglas.cms_news_backend.exception.local.EntityAlreadyExistsException
import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.model.Category.Companion.generateSlug
import douglas.cms_news_backend.model.Tag
import douglas.cms_news_backend.repository.TagRepository
import douglas.cms_news_backend.service.validations.validateTagPermissions
import douglas.cms_news_backend.utils.AuthUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val authUtil: AuthUtil
) {

    fun createTag(tagName: String) : Tag {

        val currentUser = authUtil.getCurrentUser()

        if (tagRepository.findByName(tagName).isPresent) {
            throw EntityAlreadyExistsException("Tag já cadastrada.")
        }

        val slug = generateSlug(tagName)

        val newTag = Tag(
            name = tagName,
            slug = slug,
            author = currentUser,
            createdAt = LocalDateTime.now(),
        )

        return tagRepository.save(newTag)
    }

    fun findTagByNameNoOptional(name: String): Tag {
        val tag = tagRepository.findTagByName(name)

        if(tag == null) {
            throw EntityNotFoundException("Tag não encontrada.")
        }

        return tag
    }

    fun findTagByName(name: String): Tag? {
        val tag = tagRepository.findByName(name)

        if (!tag.isPresent) {
            throw EntityNotFoundException("Tag não encontrada.")
        }

        return tag.get()
    }

    fun findAllByNames(names: List<String>): List<Tag> {
        return tagRepository.findAllByName(names)
    }

    fun findAllTags(page: Int, size: Int, sort: String = "name"): Page<TagDto> {
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(sort).ascending()
        )
        val tags : Page<Tag> = tagRepository.findAll(pageable)

        return tags.map { tag ->
            tag.author?.let {
                TagDto(
                    id = tag.id!!.toString(),
                    name = tag.name,
                    slug = tag.slug,
                    authorName = it.name,
                )
            }
        }
    }

    fun updateTag(tagName: String): Tag? {
        val currentUser = authUtil.getCurrentUser()
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

    fun deleteTag(tagName: String) {
        val currentUser = authUtil.getCurrentUser()
        val tag = tagRepository.findByName(tagName)
            .orElseThrow { EntityNotFoundException("Tag não encontrada.") }

        validateTagPermissions(tag, currentUser, "excluir")

        tagRepository.delete(tag)
    }
}