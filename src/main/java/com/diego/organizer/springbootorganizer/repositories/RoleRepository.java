package com.diego.organizer.springbootorganizer.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.diego.organizer.springbootorganizer.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long>{

    Optional<Role> findByName(String name);
}
