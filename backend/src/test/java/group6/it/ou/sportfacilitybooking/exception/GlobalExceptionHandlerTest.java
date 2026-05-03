package group6.it.ou.sportfacilitybooking.exception;

import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = Mockito.mock(WebRequest.class);
    }

    @Test
    @DisplayName("Should handle RuntimeException")
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Test runtime exception");

        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleRuntimeException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Test runtime exception", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void testHandleGlobalException() {
        Exception ex = new Exception("Test generic exception");

        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Test generic exception", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void testHandleValidationException() {
        // Arrange
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "email", "must be a well-formed email address");

        Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);
        Mockito.when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<ApiResponse<Map<String, String>>> response = exceptionHandler.handleValidationException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Dữ liệu không hợp lệ", response.getBody().getMessage());

        Map<String, String> errors = response.getBody().getData();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("must be a well-formed email address", errors.get("email"));
    }
}
