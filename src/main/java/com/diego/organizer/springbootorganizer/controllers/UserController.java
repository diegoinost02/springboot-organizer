package com.diego.organizer.springbootorganizer.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diego.organizer.springbootorganizer.entities.User;
import com.diego.organizer.springbootorganizer.services.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;

import static com.diego.organizer.springbootorganizer.security.TokenJwtConfig.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> list() {
        return this.userService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody @NonNull User user, BindingResult result) {
        if(result.hasFieldErrors()){
            return this.validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.save(user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
        user.setAdmin(false);
        return create(user, result);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Error: " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAuthenticationToken(@RequestHeader(HEADER_AUTHORIZATION) String refreshToken) {
        
        // String token = refreshToken.replace(PREFIX_TOKEN, "");
        refreshToken = refreshToken.replace("Bearer ", "");

        try {
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(refreshToken).getPayload();

            String newToken = Jwts.builder()
                .subject(claims.getSubject())
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5)))
                .issuedAt(new Date())
                .signWith(SECRET_KEY)
                .compact();
            
            String newRefreshToken = Jwts.builder()
                .subject(claims.getSubject())
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)))
                .issuedAt(new Date())
                .signWith(SECRET_KEY)
                .compact();

            Map<String, String> tokens = new HashMap<>();
            tokens.put("token", newToken);
            tokens.put("refresh_token", newRefreshToken);

            return ResponseEntity.ok(tokens);

        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JTW es invalido");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
    }
}
