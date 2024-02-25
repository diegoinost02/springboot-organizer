package com.diego.organizer.springbootorganizer.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.diego.organizer.springbootorganizer.security.filter.JwtAuthenticationFilter;
import com.diego.organizer.springbootorganizer.security.filter.JwtValidationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // para habilitar el uso de @PreAuthorize
public class SecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return this.authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests( (authz) -> authz

        // users
        .requestMatchers(HttpMethod.POST,"/api/users/register").permitAll() // registrarse
        .requestMatchers(HttpMethod.GET,"/api/users/{id}").hasAnyRole("USER", "ADMIN") // obtener user por id
        .requestMatchers(HttpMethod.GET,"/api/users/user/{username}").hasAnyRole("USER", "ADMIN") // obtener user por username
        .requestMatchers(HttpMethod.GET,"/api/users").hasRole("ADMIN") // obtener todos los users
        .requestMatchers(HttpMethod.POST, "/api/users/create").hasRole("ADMIN") // crear users con role de admin
        .requestMatchers(HttpMethod.POST, "/api/users/refresh").hasAnyRole("USER", "ADMIN") // refrescar token
        .requestMatchers(HttpMethod.PUT, "/api/users/update/{id}").hasAnyRole("USER", "ADMIN") // actualizar user
        .requestMatchers(HttpMethod.POST, "/api/users/verify-password/{id}").hasAnyRole("USER", "ADMIN") // verificar contraseña
        .requestMatchers(HttpMethod.DELETE, "/api/users/delete/{id}").hasAnyRole("USER", "ADMIN") // borrar user

        // notas
        .requestMatchers(HttpMethod.GET, "/api/notes").hasAnyRole("USER", "ADMIN") // obtener todas las notas
        .requestMatchers(HttpMethod.GET, "/api/notes/user/{userId}").hasAnyRole("USER", "ADMIN") // obtener notas por userId
        .requestMatchers(HttpMethod.GET, "/api/notes/user/{userId}/status/{status}").hasAnyRole("USER", "ADMIN") // obtener notas por userId y status
        .requestMatchers(HttpMethod.GET, "/api/notes/folder/{folderId}/status/{status}").hasAnyRole("USER", "ADMIN") // obtener notas por folderId y status
        .requestMatchers(HttpMethod.POST, "/api/notes/create").hasAnyRole("USER", "ADMIN") // crear notas
        .requestMatchers(HttpMethod.PUT, "/api/notes/update/{id}").hasAnyRole("USER", "ADMIN") // actualizar notas
        .requestMatchers(HttpMethod.DELETE, "/api/notes/delete/{id}").hasAnyRole("USER", "ADMIN") // borrar notas

        // folders
        .requestMatchers(HttpMethod.GET, "/api/folders").hasAnyRole("USER", "ADMIN") // obtener todas las carpetas
        .requestMatchers(HttpMethod.GET, "/api/folders/user/{userId}").hasAnyRole("USER", "ADMIN") // obtener carpetas por userId
        .requestMatchers(HttpMethod.POST, "/api/folders/create").hasAnyRole("USER", "ADMIN") // crear carpetas
        .requestMatchers(HttpMethod.PUT, "/api/folders/update/{id}").hasAnyRole("USER", "ADMIN") // actualizar carpetas
        .requestMatchers(HttpMethod.DELETE, "/api/folders/delete/{id}").hasAnyRole("USER", "ADMIN") // borrar carpetas

        .anyRequest().authenticated())
        .addFilter(new JwtAuthenticationFilter(this.authenticationManager())) // configuración propia
        .addFilter(new JwtValidationFilter(this.authenticationManager())) // configuración propia
        .csrf(config -> config.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // configurar el cors
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200/"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
