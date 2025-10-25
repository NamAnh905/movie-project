package com.example.movie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class CorsConfig {

    @Bean(name = "corsConfigurationSource")
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Lấy FE_ORIGIN từ biến môi trường (nếu có)
        String feOrigin = System.getenv("FE_ORIGIN");

        // Dùng patterns để hỗ trợ wildcard & nhiều domain
        List<String> patterns = new ArrayList<>();
        patterns.add("http://localhost:4200");
        if (feOrigin != null && !feOrigin.isBlank()) {
            patterns.add(feOrigin);                 // ví dụ: https://my-fe.vercel.app
        } else {
            // fallback phổ biến khi deploy FE
            patterns.add("https://*.onrender.com");
            patterns.add("https://*.vercel.app");
            patterns.add("https://*.netlify.app");
        }
        cfg.setAllowedOriginPatterns(patterns);

        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L); // cache preflight 1 giờ

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
