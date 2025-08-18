package ze.mystoragemanagement.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Author : Ze Li
 * @Date : 13/07/2025 23:21
 * @Version : V1.0
 * @Description :
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private FirebaseAuth firebaseAuth;
    @Value("${public.urls}")
    private String[] publicURLs;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getMethod().equals("OPTIONS") || isPublicPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = parseJwt(request);
            if (token != null) {
                boolean checkRevoked = !request.getMethod().equals("GET");
                FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token, checkRevoked);
                Authentication authentication = new FirebaseAuthenticationToken(
                        firebaseToken.getUid(),
                        firebaseToken.getClaims()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(),"Error: Unauthorized");
            return;
        }
        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth == null) return null;
        if (headerAuth.startsWith("Bearer ")) return headerAuth.substring(7);
        return null;
    }
    private boolean isPublicPath(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        for (String url : publicURLs) {
            if (pathMatcher.match(contextPath+url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
