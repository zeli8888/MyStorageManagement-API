package ze.mystoragemanagement.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author : Ze Li
 * @Date : 08/08/2025 02:25
 * @Version : V1.0
 * @Description :
 */
@Component
public class JwtCookieUtil {
    @Value("${jwt.token.name}")
    private String JWT_COOKIE_NAME;
    // refresh JWT token
    public void refreshJwtToken(HttpServletRequest request, HttpServletResponse response, String jwtToken) {
        Cookie jwtCookie = new Cookie(JWT_COOKIE_NAME, jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        String contextPath = request.getContextPath();
        jwtCookie.setPath(contextPath.isEmpty() ? "/" : contextPath);
        jwtCookie.setAttribute("SameSite", "Lax");
        response.addCookie(jwtCookie);
    }
}
