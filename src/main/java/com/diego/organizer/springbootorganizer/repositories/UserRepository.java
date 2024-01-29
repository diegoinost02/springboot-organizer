package com.diego.organizer.springbootorganizer.repositories;

import org.springframework.data.repository.CrudRepository;

import com.diego.organizer.springbootorganizer.entities.User;

public interface UserRepository extends CrudRepository<User, Long>{
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
