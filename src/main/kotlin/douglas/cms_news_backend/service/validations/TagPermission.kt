package douglas.cms_news_backend.service.validations

import douglas.cms_news_backend.model.Category
import douglas.cms_news_backend.model.Tag
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.extensions.isEditor
import org.springframework.security.access.AccessDeniedException

fun validateTagPermissions(tag: Tag, currentUser: User, action: String) {
    if (currentUser.isEditor()) {
        return
    }

    throw AccessDeniedException("Apenas jornalistas e editores podem $action tags")
}