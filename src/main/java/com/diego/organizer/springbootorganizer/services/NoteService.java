package com.diego.organizer.springbootorganizer.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diego.organizer.springbootorganizer.entities.Note;
import com.diego.organizer.springbootorganizer.repositories.NoteRepository;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Transactional(readOnly = true)
    public List<Note> findAll() {
        return (List<Note>)this.noteRepository.findAll();
    }
    // find by id?

    @Transactional(readOnly = true)
    public Note save(Note note) {
        return this.noteRepository.save(note);
    }

    @Transactional
    public Optional<Note> update(Long id, Note note) {
        Optional<Note> noteOptional = this.noteRepository.findById(id);
        if(noteOptional.isPresent()) {
            Note noteDb = noteOptional.orElseThrow();

            noteDb.setTitle(note.getTitle());
            noteDb.setDescription(note.getDescription());
            note.setStatus(note.isStatus());
            
            return Optional.of(this.noteRepository.save(noteDb)); //save devuelve un objeto de tipo Note, por eso se usa Optional.of
        }
        return noteOptional;
    }

    @Transactional // ver on cascade
    public Optional<Note> delete(Long id) {
        Optional<Note> noteOptional = this.noteRepository.findById(id);
        noteOptional.ifPresent(noteDb -> {
            this.noteRepository.delete(noteDb);
        });
        return noteOptional;
    }
}
