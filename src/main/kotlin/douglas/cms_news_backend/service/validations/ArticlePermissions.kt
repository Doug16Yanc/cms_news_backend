package douglas.cms_news_backend.service.validations

import douglas.cms_news_backend.model.Article
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.extensions.isEditor
import douglas.cms_news_backend.service.extensions.isJournalist
import douglas.cms_news_backend.exception.local.AccessDeniedException

fun validateArticlePermissions(article: Article, currentUser: User, action: String) {
    if (currentUser.isEditor()) {
        return
    }

    if (currentUser.isJournalist()) {
        if (article.author.id != currentUser.id) {
            throw AccessDeniedException("Jornalistas só podem $action seus próprios artigos")
        }
        return
    }

    throw AccessDeniedException("Apenas jornalistas e editores podem $action artigos")
}
