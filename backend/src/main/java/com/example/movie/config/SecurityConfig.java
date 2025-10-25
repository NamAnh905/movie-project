package com.example.movie.config;

import com.example.movie.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService; // JpaUserDetailsService của bạn

    // Bean #1: Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean #2: AuthenticationProvider (dùng UserDetailsService + PasswordEncoder)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    // Bean #3: AuthenticationManager (cho AuthController)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // KHÔNG tiêm AuthenticationProvider qua field/constructor để tránh vòng lặp
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationProvider authProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .authorizeHttpRequests(auth -> auth
                        // Preflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ===== Public GET (mở đúng pattern) =====
                        .requestMatchers(HttpMethod.GET,
                                // showtimes
                                "/api/showtimes/public", "/api/showtimes/public/**",
                                "/api/showtimes/*", "/api/showtimes/resolve",

                                // cinemas (public)
                                "/api/cinemas/public", "/api/cinemas/public/**",

                                // movies (các trang public)
                                "/api/movies",              // danh sách
                                "/api/movies/*",            // /api/movies/{id}
                                "/api/movies/**",           // phòng khi có nested route public khác
                                "/api/movies/all",
                                "/api/movies/status/*/all", // thay {status} bằng *

                                // genres (public)
                                "/api/genres/all",
                                "/api/genres/*"
                        ).permitAll()

                        // ===== Static/Auth/Docs =====
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/uploads/**"
                        ).permitAll()

                        // ===== Bookings =====
                        .requestMatchers(HttpMethod.GET,  "/api/bookings/mine").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/bookings/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bookings").authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/api/bookings/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasRole("ADMIN")

                        // ===== Close the rest =====
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
