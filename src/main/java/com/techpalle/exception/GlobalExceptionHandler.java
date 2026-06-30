package com.techpalle.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.techpalle.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //  Common builder method 
    private ApiResponse<Object> buildErrorResponse(
            String message,
            HttpStatus status,
            List<String> errors,
            String path) {

        return ApiResponse.builder()
                .success(false)
                .statusCode(status.value())
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .path(path)
                .traceId(UUID.randomUUID().toString()) 
                .build();
    }

    //  Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        ApiResponse<Object> response = buildErrorResponse(
                "Resource Not Found",
                HttpStatus.NOT_FOUND,
                List.of(ex.getMessage()),
                extractPath(request)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //  Business Exception
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex,
            WebRequest request) {

        log.warn("Business exception: {}", ex.getMessage());

        ApiResponse<Object> response = buildErrorResponse(
                "Business Error",
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage()),
                extractPath(request)
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //  Allocation Exception
    @ExceptionHandler(AllocationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAllocationException(
            AllocationException ex,
            WebRequest request) {

        log.warn("Allocation error: {}", ex.getMessage());

        ApiResponse<Object> response = buildErrorResponse(
                "Allocation Error",
                HttpStatus.CONFLICT,
                List.of(ex.getMessage()),
                extractPath(request)
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    //  Validation Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiResponse<Object> response = buildErrorResponse(
                "Validation Failed",
                HttpStatus.BAD_REQUEST,
                errors,
                extractPath(request)
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //  DB Constraint Errors
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityException(
            DataIntegrityViolationException ex,
            WebRequest request) {

        log.error("Database error", ex);

        ApiResponse<Object> response = buildErrorResponse(
                "Database Constraint Violation",
                HttpStatus.CONFLICT,
                List.of("Duplicate or invalid data"),
                extractPath(request)
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Optimistic Locking Exception 
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Object>> handleOptimisticLocking(
            ObjectOptimisticLockingFailureException ex,
            WebRequest request) {

        ApiResponse<Object> response = buildErrorResponse(
                "Concurrent update detected. Please retry.",
                HttpStatus.CONFLICT,
                List.of("Data was modified by another transaction"),
                extractPath(request)
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    //  Illegal Argument
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {

        ApiResponse<Object> response = buildErrorResponse(
                "Invalid Argument",
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage()),
                extractPath(request)
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Global Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error", ex);

        ApiResponse<Object> response = buildErrorResponse(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Unexpected error occurred"),
                extractPath(request)
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //  Utility method
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}

