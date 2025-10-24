package com.example.movie.controller.auth;

import com.example.movie.dto.request.auth.RegisterRequest;
import com.example.movie.security.JwtService;
import com.example.movie.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest dto) {
        var u = userService.register(dto);
        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (user == null || auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false, "message", "Unauthorized"
            ));
        }
        var roles = auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "authorities", roles
        ));
    }

    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String token) {}
}