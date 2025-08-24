package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String email = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            logger.info("🔑 Incoming Token: " + token);

            try {
                email = jwtUtil.extractUsername(token); // subject (email)

                // also log the role we see in incoming token
                String roleInToken = jwtUtil.extractRole(token);
                logger.info("📌 Incoming Token Email=" + email + " | Role=" + roleInToken);

                // regenerate a fresh token for comparison
                String regenerated = jwtUtil.generateToken(email, roleInToken);
                logger.info("🆕 Regenerated Token: " + regenerated);

            } catch (Exception e) {
                logger.debug("Invalid JWT while extracting username: " + e.getMessage());
            }
        }


        // If we got an email from token and no authentication exists yet
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(token, userDetails)) {
                    String role = jwtUtil.extractRole(token); // e.g. ROLE_ADMIN

                    // Build authority from token’s role
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    Collections.singletonList(authority)
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.info("✅ JWT Auth Success | Email=" + email + " | Role=" + role);
                }
            } catch (Exception e) {
                logger.debug("❌ JWT authentication failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
