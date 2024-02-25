package com.diego.organizer.springbootorganizer.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diego.organizer.springbootorganizer.entities.User;
import com.diego.organizer.springbootorganizer.services.UserSecurityService;
import com.diego.organizer.springbootorganizer.services.UserService;
import com.diego.organizer.springbootorganizer.services.dto.UpdateUserDto;
import com.diego.organizer.springbootorganizer.services.dto.VerifyPasswordDto;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;

@CrossOrigin(origins = {"http://localhost:4200/"}) 
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityService userSecurityService;


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

    @GetMapping("/user/{username}")
    public ResponseEntity<?> profile(@Valid @PathVariable String username) {
        Optional<User> userOptional = this.userService.findByUsername(username);
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userOptional.orElseThrow());
    }

    @PostMapping("/create")
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
            Map<String, String> tokens = this.userSecurityService.refreshAuthenticationToken(refreshToken);
            return ResponseEntity.ok(tokens);
        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT es inválido");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody UpdateUserDto user, @NonNull @PathVariable Long id, BindingResult result) {
        if(result.hasFieldErrors()) {
            return this.validation(result);
        }
        Optional<User> userOptional = this.userService.findById(id);
        if(!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = userOptional.get();

        if(userSecurityService.verifyPasword(user.getPassword(), existingUser) == false) {
            Map<String, String> errors = new HashMap<>();
            errors.put("password", "Error: Contraseña incorrecta");
            return ResponseEntity.badRequest().body(errors);
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        if(user.getNewPassword() != null && !user.getNewPassword().isEmpty()){
            existingUser.setPassword(user.getNewPassword());
        } else {
            existingUser.setPassword(user.getPassword());
        }

        try {
            userService.save(existingUser);
            return ResponseEntity.ok(existingUser);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("error", "Error: Username o email no disponibles");
            return ResponseEntity.badRequest().body(errors);
        }
    }
    
    @PostMapping("/verify-password/{id}") // debe ser post para enviar el RequestBody
    public ResponseEntity<?> verifyPassword(@PathVariable Long id, @RequestBody VerifyPasswordDto password) {
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User existingUser = userOptional.get();

        if (userSecurityService.verifyPasword(password.getPassword(), existingUser)) {
            return ResponseEntity.ok(existingUser);
        } else {
            return ResponseEntity.badRequest().body("Password is incorrect");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable @NonNull Long id) {
        Optional<User> userOptional = this.userService.delete(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }
    

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Error: " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
