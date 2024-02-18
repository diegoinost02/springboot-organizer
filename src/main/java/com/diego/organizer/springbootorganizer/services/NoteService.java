package com.diego.organizer.springbootorganizer.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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

    @Transactional(readOnly = true)
    public List<Note> findAllByUserId(Long userId) {
        return (List<Note>)this.noteRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Note> findAllByStatusAndFoldersId(Long folderId, boolean status){
        return (List<Note>)this.noteRepository.findAllByFoldersIdAndStatus(folderId, status);
    }

    @Transactional(readOnly = true)
    public List<Note> findAllByUserIdAndStatus(Long userId, boolean status) {
        return (List<Note>)this.noteRepository.findAllByUserIdAndStatus(userId, status);
    }


    @Transactional
    public Note save(@NonNull Note note) {
        return this.noteRepository.save(note);
    }

    @Transactional
    public Optional<Note> update(@NonNull Long id, Note note) {
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

    @Transactional
    public Optional<Note> delete(@NonNull Long id) {
        Optional<Note> noteOptional = this.noteRepository.findById(id);
        noteOptional.ifPresent(noteDb -> {
            if (noteDb != null) {
                this.noteRepository.delete(noteDb);
            }
        });
        return noteOptional;
    }
}
