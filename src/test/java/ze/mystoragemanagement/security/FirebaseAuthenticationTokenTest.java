package ze.mystoragemanagement.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

class FirebaseAuthenticationTokenTest {

    private static final String TEST_UID = "user-123";
    private static final Map<String, Object> CLAIMS_WITH_ROLES =
            Map.of("roles", List.of("admin", "moderator"));
    private static final Map<String, Object> CLAIMS_WITHOUT_ROLES =
            Map.of("email", "test@example.com");
    private static final Map<String, Object> CLAIMS_WITH_EMPTY_ROLES =
            Map.of("roles", Collections.emptyList());

    @Test
    void shouldCreateAuthenticatedTokenWithAuthorities() {
        // When
        var token = new FirebaseAuthenticationToken(TEST_UID, CLAIMS_WITH_ROLES);

        // Then
        assertThat(token.isAuthenticated()).isTrue();
        assertThat(token.getPrincipal()).isEqualTo(TEST_UID);
        assertThat(token.getCredentials()).isNull();
        assertThat(token.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN", "ROLE_MODERATOR");
    }

    @Test
    void shouldGenerateDefaultRoleWhenNoRolesInClaims() {
        // When
        var token = new FirebaseAuthenticationToken(TEST_UID, CLAIMS_WITHOUT_ROLES);

        // Then
        assertThat(token.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    void shouldHandleEmptyRolesListWithDefaultRole() {
        // When
        var token = new FirebaseAuthenticationToken(TEST_UID, CLAIMS_WITH_EMPTY_ROLES);

        // Then
        assertThat(token.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    void shouldUppercaseMixedCaseRoles() {
        // Given
        var claims = Map.of("roles", (Object) List.of("AdMiN", "MOderator"));

        // When
        var token = new FirebaseAuthenticationToken(TEST_UID, claims);

        // Then
        assertThat(token.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN", "ROLE_MODERATOR");
    }

    @Test
    void shouldContainOriginalClaims() {
        // When
        var token = new FirebaseAuthenticationToken(TEST_UID, CLAIMS_WITH_ROLES);

        // Then
        assertThat(token.getClaims())
                .containsExactlyEntriesOf(CLAIMS_WITH_ROLES);
    }

    @Test
    void shouldHandleNullClaimsSafely() {
        // When
        var token = new FirebaseAuthenticationToken(TEST_UID, null);

        // Then
        assertThat(token.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }
}
