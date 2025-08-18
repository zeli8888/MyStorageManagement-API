package ze.mystoragemanagement.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Test
    void handleResourceNotFound_ShouldReturn404Response() {
        // Arrange
        String errorMessage = "Resource not found";
        RuntimeException ex = new EmptyResultDataAccessException(errorMessage, 1);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleResourceNotFound(ex);

        // Assert
        assertResponseStructure(response, HttpStatus.NOT_FOUND, "not_found", errorMessage);
    }

    @Test
    void handleDataIntegrityViolation_ShouldReturn409Response() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Data conflict");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleDataIntegrityViolation(ex);

        // Assert
        assertResponseStructure(response, HttpStatus.CONFLICT, "data_conflict", "Data conflict occurred");
    }

    @Test
    void handleValidationExceptions_ShouldReturnFieldErrors() {
        // Arrange
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        BindingResult bindingResult = mock(BindingResult.class);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("validation_error", response.getBody().error());

        Map<?, ?> details = (Map<?, ?>) response.getBody().details();
        assertTrue(details.containsKey("field"));
        assertEquals("must not be null", details.get("field"));
    }

    @Test
    void handleValidationExceptions_ShouldReturnFieldErrors_WithoutErrorMessage() {
        // Arrange
        FieldError fieldError = new FieldError("object", "field", null);
        BindingResult bindingResult = mock(BindingResult.class);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("validation_error", response.getBody().error());

        Map<?, ?> details = (Map<?, ?>) response.getBody().details();
        assertTrue(details.containsKey("field"));
        assertEquals("Invalid value", details.get("field"));
    }

    @Test
    void handleResponseStatus_ShouldReturnCustomStatus() {
        // Arrange
        ResponseStatusException ex = new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Access denied"
        );

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleResponseStatus(ex);

        // Assert
        assertResponseStructure(response, HttpStatus.FORBIDDEN, "Access denied", "403 FORBIDDEN");
    }

    @Test
    void handleAllExceptions_ShouldReturn500Response() {
        // Arrange
        Exception ex = new NullPointerException("Unexpected error");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleAllExceptions(ex);

        // Assert
        assertResponseStructure(response, HttpStatus.INTERNAL_SERVER_ERROR,
                "internal_error", "An unexpected error occurred");
    }

    private void assertResponseStructure(ResponseEntity<GlobalExceptionHandler.ErrorResponse> response,
                                         HttpStatus expectedStatus,
                                         String expectedError,
                                         String expectedMessage) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedError, response.getBody().error());
        assertEquals(expectedMessage, response.getBody().message());
    }
}
