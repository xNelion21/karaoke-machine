package com.karaokeapp.karaoke_backend.security;

import com.karaokeapp.karaoke_backend.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Pobierz token JWT z nagłówka żądania
        String token = getJwtFromRequest(request);

        // 2. Waliduj token i ustaw autoryzację
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            // Pobierz nazwę użytkownika z tokena
            String username = tokenProvider.getUsernameFromJWT(token);

            // Załaduj dane użytkownika (role, uprawnienia)
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Utwórz obiekt uwierzytelnienia
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Ustaw uwierzytelnienie w kontekście Spring Security
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Kontynuuj łańcuch filtrów
        filterChain.doFilter(request, response);
    }

    // Pomocnicza metoda do ekstrakcji tokena "Bearer ..."
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Sprawdź, czy nagłówek Authorization istnieje i zaczyna się od "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}