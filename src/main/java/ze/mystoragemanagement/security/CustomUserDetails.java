package ze.mystoragemanagement.security;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 20:43
 * @Version : V1.0
 * @Description :
 */

public class CustomUserDetails extends User implements OAuth2User {

    private final long userId;
    private Map<String, Object> attributes;
    private String nameAttributeKey;

    public CustomUserDetails(String userName, String password, long userId, Set<GrantedAuthority> authorities) {
        super(userName, password, authorities);
        this.userId = userId;
    }

    public CustomUserDetails(OAuth2User OAuthUser, String userName, String password, long userId, Set<GrantedAuthority> authorities) {
        this(userName, password, userId, authorities);
        this.attributes = OAuthUser.getAttributes();
        this.nameAttributeKey = OAuthUser.getName();
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return this.nameAttributeKey;
    }
}