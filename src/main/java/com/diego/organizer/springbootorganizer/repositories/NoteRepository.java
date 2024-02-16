package com.diego.organizer.springbootorganizer.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.diego.organizer.springbootorganizer.entities.Note;

public interface NoteRepository extends CrudRepository<Note, Long>{
    
    List<Note> findAllByUserId(Long userId);

    List<Note> findAllByUserIdAndStatus(Long userId, boolean status);

    List<Note> findAllByFoldersIdAndStatus(Long folderId, boolean status);
}
