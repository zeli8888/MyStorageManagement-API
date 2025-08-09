package ze.mystoragemanagement.security;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author : Ze Li
 * @Date : 09/08/2025 21:04
 * @Version : V1.0
 * @Description :
 */

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    private final String uid;
    private final Map<String, Object> claims;

    public FirebaseAuthenticationToken(String uid, Map<String, Object> claims) {
        super(extractAuthorities(claims));
        this.uid = uid;
        this.claims = claims;
        super.setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> extractAuthorities(Map<String, Object> claims) {
        List<String> roles = (List<String>) claims.getOrDefault("roles", Collections.emptyList());

        if (roles.isEmpty()) {
            roles = Collections.singletonList("user");
        }

        return roles.stream().map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Object getCredentials() { return null; }

    @Override
    public Object getPrincipal() { return uid; }

    public Map<String, Object> getClaims() { return claims; }
}
