package douglas.cms_news_backend.controller

import douglas.cms_news_backend.dto.CreateUserDto
import douglas.cms_news_backend.dto.UserDto
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/create-user")
    fun createUser(@RequestBody createUserDto: CreateUserDto): ResponseEntity<String> {
        val user = userService.createUser(createUserDto)

        return ResponseEntity("Usuário " + user?.name + ", " + user?.role?.name + " criado com sucesso!", HttpStatus.CREATED)
    }

    @GetMapping("/find-user/{email}")
    fun findUserByEmail(@PathVariable email: String): ResponseEntity<UserDto> {
        val user = userService.findUserByEmail(email)
        return ResponseEntity.ok().body(user)
    }

    @GetMapping("/findAllUsers")
    fun getAllUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort: String
    ): Page<UserDto> {
        val pageable = PageRequest.of(page, size, Sort.by(sort).ascending())
        return userService.findAllUsers(page, size, sort)
    }

    @GetMapping("/findAllUsers/{roleName}")
    fun getAllUsersByRoleName(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort: String,
        @PathVariable roleName: String
    ): Page<UserDto> {
        val pageable = PageRequest.of(page, size, Sort.by(sort).ascending())
        return userService.findUsersByRole(roleName, page, size, sort)
    }
}