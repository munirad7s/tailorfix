package de.tailorfix.api

import de.tailorfix.domain.ConflictException
import de.tailorfix.domain.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

/**
 * Errors follow RFC 9457 "Problem Details" (`application/problem+json`), as required by the
 * Zalando RESTful API Guidelines ("MUST support problem JSON").
 */
@RestControllerAdvice
class ProblemHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(NotFoundException::class)
    fun notFound(e: NotFoundException): ProblemDetail =
        problem(HttpStatus.NOT_FOUND, "Resource not found", e.message, "not-found")

    @ExceptionHandler(ConflictException::class)
    fun conflict(e: ConflictException): ProblemDetail =
        problem(HttpStatus.CONFLICT, "Conflict", e.message, "conflict")

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(e: IllegalArgumentException): ProblemDetail =
        problem(HttpStatus.BAD_REQUEST, "Invalid request", e.message, "invalid-request")

    private fun problem(status: HttpStatus, title: String, detail: String?, type: String): ProblemDetail =
        ProblemDetail.forStatusAndDetail(status, detail ?: title).apply {
            this.title = title
            this.type = URI.create("https://tailorfix.dev/problems/$type")
        }
}
