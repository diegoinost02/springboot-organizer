package com.diego.organizer.springbootorganizer.services;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diego.organizer.springbootorganizer.entities.User;
import com.diego.organizer.springbootorganizer.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import static com.diego.organizer.springbootorganizer.security.TokenJwtConfig.*;


@Service
public class UserSecurityService implements UserDetailsService{ // busca al usuario en la base de datos para loguearse

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByUsername(username);

        if(!userOptional.isPresent()){
            throw new UsernameNotFoundException("Username not found");
        }
        User user = userOptional.orElseThrow();

        List<GrantedAuthority> authorities = user.getRoles()
        .stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            true,
            true,
            true,
            authorities);
    }

    @Transactional
    public Map<String, String> refreshAuthenticationToken(String refreshToken) {
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

            Map<String, String> tokens = new LinkedHashMap<>();
            tokens.put("token", newToken);
            tokens.put("refresh_token", newRefreshToken);

            return tokens;

        } catch (JwtException e) {
            throw new JwtException("El token JWT es inválido", e);
        }
    }

    @Transactional
    public boolean verifyPasword(String password, User existingUser){
        if (BCrypt.checkpw(password, existingUser.getPassword())) {
            return true;
        } else {
            return false;
        }
    }
}
 