package douglas.cms_news_backend.exception.global

import douglas.cms_news_backend.exception.local.BadCredentialsException
import douglas.cms_news_backend.exception.local.BadRequestException
import douglas.cms_news_backend.exception.local.EntityAlreadyExistsException
import douglas.cms_news_backend.exception.local.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityAlreadyExistsException::class)
    fun handleEntityAlreadyExistsException(e: EntityAlreadyExistsException): ResponseEntity<ErrorResponse> {
        val body : ErrorResponse? = e.message?.let { ErrorResponse(it) }
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(e: EntityNotFoundException): ResponseEntity<ErrorResponse> {
        val body : ErrorResponse? = e.message?.let { ErrorResponse(it) }
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ErrorResponse> {
        val body : ErrorResponse? = e.message?.let { ErrorResponse(it) }
        return ResponseEntity(body, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<ErrorResponse> {
        val body : ErrorResponse? = e.message?.let { ErrorResponse(it) }
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ErrorResponse> {
        val body : ErrorResponse? = e.message?.let { ErrorResponse(it) }
        return ResponseEntity(body, HttpStatus.FORBIDDEN)
    }
}