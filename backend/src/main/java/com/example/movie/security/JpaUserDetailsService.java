// src/main/java/com/example/movie/security/JpaUserDetailsService.java
package com.example.movie.security;

import com.example.movie.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // normalize role từ DB: trim + upper + thêm ROLE_ nếu thiếu
        String raw = (u.getRole() == null ? "USER" : u.getRole()).trim().toUpperCase();
        String role = raw.startsWith("ROLE_") ? raw : ("ROLE_" + raw);

        var auth = new SimpleGrantedAuthority(role);

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(auth)     // <- QUAN TRỌNG: phải là ROLE_*
                .accountExpired(false).accountLocked(false)
                .credentialsExpired(false).disabled(false)
                .build();
    }
}

