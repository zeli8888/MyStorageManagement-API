package ze.mystoragemanagement.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FirebaseSecurityContextIdTest {

    private final FirebaseSecurityContextId contextId = new FirebaseSecurityContextId();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentFirebaseId_ShouldThrowWhenNoAuthentication() {
        // Setup empty security context
        SecurityContextHolder.setContext(mock(SecurityContext.class));

        assertThrowsExactly(InsufficientAuthenticationException.class,
                contextId::getCurrentFirebaseId,
                "Should throw when no authentication");
    }

    @Test
    void getCurrentFirebaseId_ShouldThrowWhenPrincipalNotString() {
        // Mock authentication with non-string principal
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(12345L);  // Long principal

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        assertThrowsExactly(InsufficientAuthenticationException.class,
                contextId::getCurrentFirebaseId,
                "Should throw when principal is not String");
    }

    @Test
    void getCurrentFirebaseId_ShouldThrowWhenPrincipalNull() {
        // Mock authentication with null principal
        Authentication auth = mock(Authentication.class);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        assertThrowsExactly(InsufficientAuthenticationException.class,
                contextId::getCurrentFirebaseId,
                "Should throw when principal is null");
    }

    @Test
    void getCurrentFirebaseId_ShouldReturnIdWhenValidPrincipal() {
        // Mock valid authentication
        String expectedUid = "firebase-uid-123";
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(expectedUid);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        String actualUid = contextId.getCurrentFirebaseId();

        assertEquals(expectedUid, actualUid,
                "Should return correct UID from principal");
    }
}
