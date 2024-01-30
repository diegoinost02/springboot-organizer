package com.diego.organizer.springbootorganizer.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diego.organizer.springbootorganizer.entities.User;
import com.diego.organizer.springbootorganizer.services.UserSecuritySerevice;
import com.diego.organizer.springbootorganizer.services.UserService;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSecuritySerevice userSecuritySerevice;


    @GetMapping
    public List<User> list() {
        return this.userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> view(@Valid @PathVariable Long id) {
        Optional<User> userOptional = this.userService.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userOptional.orElseThrow());
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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAuthenticationToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            Map<String, String> tokens = this. userSecuritySerevice.refreshAuthenticationToken(refreshToken);
            return ResponseEntity.ok(tokens);
        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT es inválido");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Error: " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
