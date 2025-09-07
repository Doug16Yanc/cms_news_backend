package douglas.cms_news_backend.controller

import douglas.cms_news_backend.service.RoleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roles")
class RoleController(
    private val roleService: RoleService
) {

    @PostMapping("/create-role/{roleName}")
    fun createRole(@PathVariable roleName: String): ResponseEntity<String> {
        var newRole = roleService.create(roleName)

        return ResponseEntity.ok().body("Role criada com sucesso : " + newRole.name)
    }
}