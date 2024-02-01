package com.diego.organizer.springbootorganizer.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diego.organizer.springbootorganizer.entities.Role;
import com.diego.organizer.springbootorganizer.entities.User;
import com.diego.organizer.springbootorganizer.repositories.RoleRepository;
import com.diego.organizer.springbootorganizer.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
 

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>)this.userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        if(id == null) {
            return Optional.empty();
        }
        return this.userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        if(username == null) {
            return Optional.empty();
        }
        return this.userRepository.findByUsername(username);
    }

    @Transactional
    public User save(@NonNull User user) {
        Optional<Role> roleOptional = this.roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();

        roleOptional.ifPresent(role -> roles.add(role));

        if(user.isAdmin()) {
            Optional<Role> roleAdminOptional = this.roleRepository.findByName("ROLE_ADMIN");
            roleAdminOptional.ifPresent(role -> roles.add(role));
        }
        user.setRoles(roles);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        return this.userRepository.save(user);
    }

    @Transactional
    public Optional<User> update(@NonNull Long id, User user) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if(userOptional.isPresent()) {
            User userDb = userOptional.orElseThrow();

            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            userDb.setPassword(user.getPassword());
            
            return Optional.of(this.userRepository.save(userDb)); //save devuelve un objeto de tipo User, por eso se usa Optional.of
        }
        return userOptional;
    }

    @Transactional
    public Optional<User> delete(@NonNull Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        userOptional.ifPresent(userDb -> {
            if (userDb != null) {
                this.userRepository.delete(userDb);
            }
        });
        return userOptional;
    }

    public boolean existByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }
    public boolean existByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }
}
