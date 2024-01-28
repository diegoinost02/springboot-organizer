package com.diego.organizer.springbootorganizer.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.diego.organizer.springbootorganizer.entities.Folder;


public interface FolderRepository extends CrudRepository<Folder, Long>{

    List<Folder> findAllByUserId(Long userId);
}
