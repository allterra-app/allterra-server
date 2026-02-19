package com.allterra.server.exception;

import com.allterra.server.authentication.exception.InvalidCredentialsException;
import com.allterra.server.authentication.exception.InvalidRefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handler for exceptions.
 */
@Slf4j
@ControllerAdvice
public final class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyTaken(
            final EmailAlreadyTakenException exception,
            final HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<ApiErrorResponse> handleUsernameAlreadyTaken(
            final UsernameAlreadyTakenException exception,
            final HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            final ResourceNotFoundException exception,
            final HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidRefreshTokenException.class})
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            final RuntimeException exception,
            final HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            final AccessDeniedException exception,
            final HttpServletRequest request
    ) {
        var message = "You do not have permission to access this resource.";
        return buildResponse(HttpStatus.FORBIDDEN, message, request);
    }

    /**
     * Handles body validation errors.
     *
     * @param exception validation exception
     * @param request HTTP request
     * @return unified error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception,
            final HttpServletRequest request
    ) {
        var validationErrors = new LinkedHashMap<String, String>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }
        var response = ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                "Validation failed for request body.",
                request.getRequestURI(),
                validationErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles parameter/path/query validation errors.
     *
     * @param exception validation exception
     * @param request HTTP request
     * @return unified error response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            final ConstraintViolationException exception,
            final HttpServletRequest request
    ) {
        var validationErrors = new LinkedHashMap<String, String>();
        exception.getConstraintViolations().forEach(v ->
                validationErrors.put(v.getPropertyPath().toString(), v.getMessage()));

        var response = ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                "Validation failed for request parameters.",
                request.getRequestURI(),
                validationErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            NullPointerException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            final Exception exception,
            final HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedMediaType(
            final HttpMediaTypeNotSupportedException exception,
            final HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, exception.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(
            final NoResourceFoundException exception,
            final HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(
            final Exception exception,
            final HttpServletRequest request
    ) {
        log.error("Unhandled exception", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.", request);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            final HttpStatus status,
            final String message,
            final HttpServletRequest request
    ) {
        var response = ApiErrorResponse.of(status, message, request.getRequestURI());
        if (status.is5xxServerError()) {
            log.error("{}: {}", status.value(), message);
        } else {
            log.debug("{}: {}", status.value(), message);
        }
        return ResponseEntity.status(status).body(response);
    }
}
