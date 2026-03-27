package com.example.movie.service.auth;

import com.example.movie.dto.request.auth.AuthRequest;
import com.example.movie.dto.response.shared.AuthResponse;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.entity.InvalidatedToken;
import com.example.movie.repository.InvalidatedTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    AuthenticationManager authManager;
    JwtService jwtService;
    UserDetailsService userDetailsService;
    InvalidatedTokenRepository invalidatedTokenRepository;

    public AuthResponse login(AuthRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        UserDetails user = userDetailsService.loadUserByUsername(req.getUsername());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);

        String jwtId = jwtService.extractJwtId(refreshToken);
        if (!jwtService.isTokenValid(refreshToken, user) || invalidatedTokenRepository.existsById(jwtId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    public void logout(String accessToken) {
        try {
            String jwtId = jwtService.extractJwtId(accessToken);
            InvalidatedToken token = InvalidatedToken.builder()
                    .id(jwtId)
                    .expiryTime(jwtService.extractExpiration(accessToken))
                    .build();
            invalidatedTokenRepository.save(token);
        } catch (Exception e) {
        }
    }
}