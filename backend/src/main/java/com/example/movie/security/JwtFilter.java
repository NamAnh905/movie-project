package com.example.movie.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        // 1) Cho qua ngay preflight
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        try {
            String header = req.getHeader("Authorization");
            if (header != null) {
                header = header.trim();
            }
            if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
                String raw = header.substring(7).trim();
                // 2) Bỏ qua token rỗng / "null" / "undefined"
                if (!raw.isEmpty() && !"null".equalsIgnoreCase(raw) && !"undefined".equalsIgnoreCase(raw)) {

                    String username = jwtService.extractUsername(raw); // có thể throw -> đã catch dưới
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        var ud = userDetailsService.loadUserByUsername(username);
                        if (jwtService.isTokenValid(raw, ud)) {
                            var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // Không sendError -> coi như request guest, để rule permitAll() xử lý
        }

        chain.doFilter(req, res);
    }
}
