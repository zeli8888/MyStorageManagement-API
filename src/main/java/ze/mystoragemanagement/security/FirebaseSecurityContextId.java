package ze.mystoragemanagement.security;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * @Author : Ze Li
 * @Date : 10/08/2025 21:36
 * @Version : V1.0
 * @Description :
 */
@Component
public class FirebaseSecurityContextId {
    public String getCurrentFirebaseId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElseThrow(() -> new InsufficientAuthenticationException("Authentication not found"));
    }
}
