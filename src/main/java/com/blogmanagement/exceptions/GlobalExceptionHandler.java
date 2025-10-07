package com.blogmanagement.exceptions;

import com.blogmanagement.dtos.responses.ResponseObject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleBadRequest(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid request body: " + ex.getLocalizedMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException ex) {
        String message = String.format("Missing parameter: %s", ex.getParameterName());
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Validation failed")
                        .data(errors)
                        .build()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        return build(HttpStatus.BAD_REQUEST, "Validation failed: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleEnumErrors(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value for parameter '%s': '%s'", ex.getName(), ex.getValue());
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method not supported: " + ex.getMethod());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        ex.printStackTrace(); // Log lỗi chi tiết ra console
        return build(HttpStatus.INTERNAL_SERVER_ERROR,  ex.getMessage());
    }

    private ResponseEntity<ResponseObject> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                ResponseObject.builder()
                        .status(status)
                        .message(message)
                        .build()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ex.getMostSpecificCause();
        String msg = ex.getMostSpecificCause().getMessage();

        if (msg != null) {
            if (msg.toLowerCase().contains("truncated")) {
                return build(HttpStatus.BAD_REQUEST, "Data too long for column");
            }
            if (msg.toLowerCase().contains("unique")) {
                return build(HttpStatus.BAD_REQUEST, "Duplicate value not allowed");
            }
            if (msg.toLowerCase().contains("cannot insert the value null")) {
                return build(HttpStatus.BAD_REQUEST, "Missing required field");
            }
        }

        return build(HttpStatus.BAD_REQUEST, "Invalid data");
    }

}
