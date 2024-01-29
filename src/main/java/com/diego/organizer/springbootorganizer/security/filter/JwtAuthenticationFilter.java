package com.diego.organizer.springbootorganizer.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.diego.organizer.springbootorganizer.entities.User;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.diego.organizer.springbootorganizer.security.TokenJwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{ // crear token

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

                User user = null;
                String username = null;
                String password = null;

                try {
                    user = new ObjectMapper().readValue(request.getInputStream(), User.class);
                    username = user.getUsername();
                    password = user.getPassword();

                } catch (StreamReadException e) { // error de lectura del stream
                    e.printStackTrace();
                } catch (DatabindException e) { // error al pasar los valores del json a la clase User
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return this.authenticationManager.authenticate(authToken); // realiza la autenticacion
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
                String username = user.getUsername();

                Collection<? extends GrantedAuthority> roles = user.getAuthorities();

                Claims claims = Jwts.claims().add("authorities", new ObjectMapper().writeValueAsString(roles)).build();

                String token = Jwts.builder()
                    .subject(username)
                    .claims(claims)
                    .expiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5)))
                    .issuedAt(new Date())
                    .signWith(SECRET_KEY)
                    .compact();

                String refreshToken = Jwts.builder()
                    .subject(username)
                    .claims(claims)
                    .expiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)))
                    .issuedAt(new Date())
                    .signWith(SECRET_KEY)
                    .compact();

                response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

                Map<String, String> body = new HashMap<>();
                body.put("token", token);
                body.put("refresh_token", refreshToken);
                body.put("username", username);
                body.put("message", String.format("Inicio de sesion del usuario %s exitoso", username));

                response.getWriter().write(new ObjectMapper().writeValueAsString(body)); // genera el json
                response.setContentType(CONTENT_TYPE); // "application/json"
                response.setStatus(200);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
                Map<String, String> body = new HashMap<>();
                body.put("message", "Error de autenticacion: username o password incorrectos");
                body.put("error", failed.getMessage());

                response.getWriter().write(new ObjectMapper().writeValueAsString(body));
                response.setContentType(CONTENT_TYPE);
                response.setStatus(401); // unauthorized
    }

}
