package com.diego.organizer.springbootorganizer.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diego.organizer.springbootorganizer.entities.User;
import com.diego.organizer.springbootorganizer.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

    @Transactional
    public User save(@NonNull User user) {
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
}
