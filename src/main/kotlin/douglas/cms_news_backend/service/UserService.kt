package douglas.cms_news_backend.service

import douglas.cms_news_backend.dto.CreateUserDto
import douglas.cms_news_backend.dto.UserDto
import douglas.cms_news_backend.exception.local.EntityAlreadyExistsException
import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.model.Role
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.repository.RoleRepository
import douglas.cms_news_backend.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val roleService: RoleService
) {

    fun createUser(createUserDto: CreateUserDto): User? {
        val existingUser = userRepository.findByEmail(createUserDto.email)
        if (existingUser != null) {
            throw EntityAlreadyExistsException("Usuário já cadastrado.")
        }

        val encryptedPassword = bCryptPasswordEncoder.encode(createUserDto.password)

        val roleName = try {
            Role.Values.valueOf(createUserDto.role).value
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Role inválida: ${createUserDto.role}. Valores permitidos: ${Role.Values.entries.joinToString()}")
        }

        val role = roleService.getByName(roleName)

        val user = role?.let {
            User(
                name = createUserDto.name,
                email = createUserDto.email,
                passwordHash = encryptedPassword,
                role = it,
                active = true
            )
        }

        return user?.let { userRepository.save(it) }
    }

    fun  findUserByEmail(email : String) : UserDto {
        val foundUser = userRepository.findByEmail(email) ?: throw EntityNotFoundException("Usuário não encontrado.")

        return UserDto(
            foundUser.name,
            foundUser.email,
            foundUser.role.name
        )
    }

    fun findUserEntityById(id: ObjectId): User {
        val foundUser = userRepository.findUserById(id) ?: throw EntityNotFoundException("Usuário não encontrado.")

        return foundUser
    }


    fun findUserEntityByEmail(email: String): User {
        val foundUser = userRepository.findByEmail(email) ?: throw EntityNotFoundException("Usuário não encontrado.")

        return foundUser
    }

    fun findUserById(id : ObjectId) : UserDto {
        val foundUser = userRepository.findUserById(id)

        return UserDto(
            foundUser.name,
            foundUser.email,
            foundUser.role.name
        )
    }

    fun findAllUsers(page: Int, size: Int, sort: String = "name"): Page<UserDto> {
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(sort).ascending()
        )

        val users : Page<User> = userRepository.findAll(pageable)
        return users.map {user ->
            UserDto(
                name = user.name,
                email = user.email,
                role = user.role.name
            )
        }
    }

    fun findUsersByRole(roleName: String, page: Int, size: Int, sort: String = "name"): Page<UserDto> {
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(sort).ascending()
        )
        val users : Page<User> = userRepository.findByRoleName(roleName, pageable)
        return users.map {user ->
            UserDto(
                name = user.name,
                email = user.email,
                role = user.role.name
            )
        }
    }
}