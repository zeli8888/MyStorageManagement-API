package ze.mystoragemanagement.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

/**
 * @Author : Ze Li
 * @Date : 05/07/2025 17:30
 * @Version : V1.0
 * @Description :
 */

@Component
public class UnAuthEntryPointJwt implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error: Unauthorized");
    }
}
