package ze.mystoragemanagement.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author : Ze Li
 * @Date : 10/08/2025 23:58
 * @Version : V1.0
 * @Description :
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // handle resource not found
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        Instant.now(),
                        "not_found",
                        ex.getMessage(),
                        null
                ));
    }

    // handle constrain violation
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        String constraintName = extractConstraintName(ex);
        String message = resolveConflictMessage(constraintName);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        Instant.now(),
                        "data_conflict",
                        message,
                        Map.of("constraint", constraintName)
                ));
    }

    // handle data validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ?
                                error.getDefaultMessage() : "Invalid value"
                ));

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        "validation_error",
                        "Invalid request content",
                        errors
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ErrorResponse(
                        Instant.now(),
                        ex.getReason(),
                        ex.getStatusCode().toString(),
                        null
                ));
    }

    // handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        Instant.now(),
                        "internal_error",
                        "An unexpected error occurred",
                        Map.of("detail", ex.getMessage())
                ));
    }

    // resolve constraint name
    private String extractConstraintName(DataIntegrityViolationException ex) {
        return Optional.ofNullable(ex.getCause())
                .filter(ConstraintViolationException.class::isInstance)
                .map(ConstraintViolationException.class::cast)
                .map(e -> {
                    ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();
                    return violation.getConstraintDescriptor()
                            .getAnnotation().annotationType().getSimpleName();
                })
                .orElse("unknown_constraint");
    }

    // resolve constraint message from constraint name
    private String resolveConflictMessage(String constraintName) {
        return ConstraintMapping.getFriendlyMessage(constraintName)
                .orElse("Data conflict occurred");
    }

    // error response object
    public record ErrorResponse(
            Instant timestamp,
            String error,
            String message,
            Object details
    ) {}

    // constraint mapping
    private enum ConstraintMapping {
        UNIQUE_INGREDIENT("unique_ingredient_per_user", "Ingredient already exists"),
        UNIQUE_DISH("unique_dish_per_user", "Dish name already used");

        private final String constraintName;
        private final String message;

        ConstraintMapping(String constraintName, String message) {
            this.constraintName = constraintName;
            this.message = message;
        }

        public static Optional<String> getFriendlyMessage(String constraintName) {
            return Arrays.stream(values())
                    .filter(cm -> cm.constraintName.equalsIgnoreCase(constraintName))
                    .map(cm -> cm.message)
                    .findFirst();
        }
    }
}


