package ze.mystoragemanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import ze.mystoragemanagement.service.impl.UserServiceImpl;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Author : Ze Li
 * @Date : 13/07/2025 23:21
 * @Version : V1.0
 * @Description :
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtils;
    @Autowired
    private JwtCookieUtil jwtCookieUtil;
    @Autowired
    private UserServiceImpl userService;
    @Value("${jwt.token.name}")
    private String JWT_COOKIE_NAME;
    @Value("${public.urls}")
    private String[] publicURLs;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (isPublicPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = parseJwt(request);
            if (token != null) {
                String validatedJwtToken = jwtUtils.validateJwtToken(token);
                if (!validatedJwtToken.equals(token)) {
                    jwtCookieUtil.refreshJwtToken(request, response, validatedJwtToken);
                }
                String username = jwtUtils.getUsernameFromToken(token);
                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot set user authentication: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
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
