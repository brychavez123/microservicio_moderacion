package com.duoc.moderacion.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.duoc.moderacion.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Seguridad base del arquetipo: HTTP Basic + BCrypt contra la tabla USUARIO compartida,
 * API sin estado, CORS configurable y errores 401/403 en JSON.
 *
 * Roles en ms-moderacion:
 * - MODERADOR: ver contenido, banear/desbanear temas y comentarios, ver historial y resumen.
 * - ADMIN: lo anterior + eliminar registros del historial.
 * - USUARIO: sin acceso (403).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.origenes:http://localhost:4200}")
    private String[] origenesPermitidos;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper mapper) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/api/info", "/error").permitAll()
                        // Borrar registros del historial: solo ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/moderacion/historial/**").hasRole("ADMIN")
                        // Todo el panel de moderación: MODERADOR y ADMIN
                        .requestMatchers("/api/moderacion/**").hasAnyRole("ADMIN", "MODERADOR")
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint((req, res, ex) -> escribirError(mapper, req, res,
                        HttpStatus.UNAUTHORIZED, "Debes autenticarte con un email y contraseña válidos")))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> escribirError(mapper, req, res,
                                HttpStatus.UNAUTHORIZED, "Debes autenticarte con un email y contraseña válidos"))
                        .accessDeniedHandler((req, res, e) -> escribirError(mapper, req, res,
                                HttpStatus.FORBIDDEN, "Tu rol no tiene permisos para realizar esta acción")));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(origenesPermitidos));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private static void escribirError(ObjectMapper mapper, HttpServletRequest req, HttpServletResponse res,
            HttpStatus status, String mensaje) throws IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        mapper.writeValue(res.getOutputStream(),
                ErrorResponse.of(status.value(), status.getReasonPhrase(), mensaje, req.getRequestURI()));
    }
}
