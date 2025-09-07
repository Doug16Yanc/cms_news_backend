package douglas.cms_news_backend.service.validations

import douglas.cms_news_backend.model.Article
import douglas.cms_news_backend.model.Category
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.extensions.isEditor
import douglas.cms_news_backend.service.extensions.isJournalist
import org.springframework.security.access.AccessDeniedException

fun validateCategoryPermissions(category: Category, currentUser: User, action: String) {
    if (currentUser.isEditor()) {
        return
    }

    throw AccessDeniedException("Apenas jornalistas e editores podem $action categorias")
}
