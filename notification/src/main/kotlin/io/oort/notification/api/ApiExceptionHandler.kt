package io.oort.notification.api

import io.oort.notification.application.NotificationNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(NotificationNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotificationNotFound(exception: NotificationNotFoundException): ApiErrorResponse =
        ApiErrorResponse(message = exception.message.orEmpty())
}

data class ApiErrorResponse(
    val message: String,
)
