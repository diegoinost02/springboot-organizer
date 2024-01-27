package com.diego.organizer.springbootorganizer.repositories;

import org.springframework.data.repository.CrudRepository;

import com.diego.organizer.springbootorganizer.entities.Note;

public interface NoteRepository extends CrudRepository<Note, Long>{

}
