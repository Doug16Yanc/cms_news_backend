package douglas.cms_news_backend.service.extensions

import douglas.cms_news_backend.model.Article
import douglas.cms_news_backend.model.User

fun User.hasJournalistOrEditorRole(): Boolean {
    return this.isJournalist() || this.isEditor()
}

fun User.isJournalist(): Boolean {
    return this.role.name.equals("JORNALISTA", ignoreCase = true)
}

fun User.isEditor(): Boolean {
    return this.role.name.equals("EDITOR", ignoreCase = true)
}

fun User.isVisitor() : Boolean {
    return this.role.name.equals("VISITANTE", ignoreCase = true)
}

fun User.canEditArticle(article: Article): Boolean {
    return this.isEditor() || (this.isJournalist() && article.author.id == this.id)
}

fun User.canDeleteArticle(article: Article): Boolean {
    return this.isEditor() || (this.isJournalist() && article.author.id == this.id)
}