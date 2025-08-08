package ze.mystoragemanagement.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ze.mystoragemanagement.model.User;
import ze.mystoragemanagement.security.JwtUtil;
import ze.mystoragemanagement.service.UserService;

import java.net.URI;

/**
 * @Author : Ze Li
 * @Date : 07/08/2025 13:56
 * @Version : V1.0
 * @Description :
 */

@RestController
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/auth/login")
    public ResponseEntity<User> login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        response.setHeader("Authorization", "Bearer " + jwtUtil.generateToken(userDetails.getUsername()));

        return ResponseEntity.ok(userService.getUser());
    }

    @PostMapping("/auth/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/user")
                .build()
                .toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }
}
