package group6.it.ou.sportfacilitybooking.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider Unit Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // Inject values using ReflectionTestUtils since this is a unit test without
        // Spring Context
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
                "mySecretKeyForSportFacilityBooking2026MySecretKeyForSportFacilityBooking2026");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 86400000L); // 24 hours
    }

    @Test
    @DisplayName("Should generate valid token")
    void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(1L, "test@example.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should extract email from token")
    void testGetEmailFromToken() {
        String token = jwtTokenProvider.generateToken(1L, "test@example.com");

        String email = jwtTokenProvider.getEmailFromToken(token);

        assertEquals("test@example.com", email);
    }

    @Test
    @DisplayName("Should extract userId from token")
    void testGetUserIdFromToken() {
        String token = jwtTokenProvider.generateToken(1L, "test@example.com");

        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        assertEquals(1L, userId);
    }

    @Test
    @DisplayName("Should validate valid token")
    void testValidateTokenValid() {
        String token = jwtTokenProvider.generateToken(1L, "test@example.com");

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void testValidateTokenInvalid() {
        boolean isValid = jwtTokenProvider.validateToken("invalid.token.string");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle parsing errors gracefully")
    void testParsingErrors() {
        assertNull(jwtTokenProvider.getEmailFromToken("invalid"));
        assertNull(jwtTokenProvider.getUserIdFromToken("invalid"));
    }
}
