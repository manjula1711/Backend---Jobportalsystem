package com.manjula.jobportal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(Customizer.withDefaults()) // ✅ Enable CORS from corsConfigurationSource()
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ✅ Allow preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/").permitAll()

                // ✅ Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/jobs/**").permitAll()

                // ✅ Role protected
                .requestMatchers("/api/recruiter/**").hasAuthority("RECRUITER")
                .requestMatchers("/api/seeker/**").hasAuthority("SEEKER")
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                .anyRequest().authenticated()
            );

        // ✅ JWT FILTER
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ CORS CONFIG (Vercel + Localhost)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ IMPORTANT:
        // Use allowedOriginPatterns to support Vercel preview URLs
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "https://frontend-jobportalsystem.vercel.app",
                "https://*.vercel.app"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ✅ allow all headers (preflight will succeed)
        config.setAllowedHeaders(List.of("*"));

        // ✅ if you send JWT in response header
        config.setExposedHeaders(List.of("Authorization"));

        // ✅ allow cookies/credentials if needed
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
