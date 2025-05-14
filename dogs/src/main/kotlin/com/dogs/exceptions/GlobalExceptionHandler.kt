package com.dogs.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handle custom exceptions like `ResponseStatusException`.
     * Typically thrown for HTTP 404, 400, etc.
     */
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = ex.statusCode.value(),
                error = ex.statusCode.toString(),
                message = ex.reason ?: "An error occurred",
                timestamp = LocalDateTime.now()
            ),
            ex.statusCode
        )
    }

    /**
     * Handle exceptions when the external API fails to respond correctly.
     */
    @ExceptionHandler(WebClientResponseException::class)
    fun handleWebClientResponseException(ex: WebClientResponseException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = ex.statusCode.value(),
                error = ex.statusCode.toString(),
                message = "Error during API call: ${ex.responseBodyAsString}",
                timestamp = LocalDateTime.now()
            ),
            ex.statusCode
        )
    }

    /**
     * Handle network-related exceptions (e.g., connection failure).
     */
    @ExceptionHandler(WebClientRequestException::class)
    fun handleWebClientRequestException(ex: WebClientRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.SERVICE_UNAVAILABLE.value(),
                error = HttpStatus.SERVICE_UNAVAILABLE.reasonPhrase,
                message = "Unable to connect to external API: ${ex.message}",
                timestamp = LocalDateTime.now()
            ),
            HttpStatus.SERVICE_UNAVAILABLE
        )
    }

    /**
     * Handle invalid input (bad request errors like malformed requests).
     */
    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(ex: ServerWebInputException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = "Invalid input: ${ex.reason}",
                timestamp = LocalDateTime.now()
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    /**
     * Handle all other exceptions (fallback for unhandled exceptions).
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                message = "Unexpected error occurred: ${ex.message}",
                timestamp = LocalDateTime.now()
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
