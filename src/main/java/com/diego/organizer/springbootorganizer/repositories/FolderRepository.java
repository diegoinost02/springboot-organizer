package com.diego.organizer.springbootorganizer.repositories;

import org.springframework.data.repository.CrudRepository;

import com.diego.organizer.springbootorganizer.entities.Folder;

public interface FolderRepository extends CrudRepository<Folder, Long>{

}
