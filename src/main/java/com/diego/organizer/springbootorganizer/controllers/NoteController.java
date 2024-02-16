package com.diego.organizer.springbootorganizer.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diego.organizer.springbootorganizer.entities.Note;
import com.diego.organizer.springbootorganizer.services.NoteService;

import jakarta.validation.Valid;

@CrossOrigin(origins = {"http://localhost:4200"}) 
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public List<Note> list() {
        return this.noteService.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Note> listByUserId(@PathVariable Long userId) {
        return this.noteService.findAllByUserId(userId);
    }

    @GetMapping("/folder/{folderId}/status/{status}") // !!
    public List<Note> listByFolderIdAndStatus(@PathVariable Long folderId, @PathVariable boolean status) {
        return this.noteService.findAllByStatusAndFoldersId(folderId, status);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public List<Note> listByUserIdAndStatus(@PathVariable Long userId, @PathVariable boolean status) {
        return this.noteService.findAllByUserIdAndStatus(userId, status);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody @NonNull Note note, BindingResult result) {
        if(result.hasFieldErrors()){
            return this.validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.noteService.save(note));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Note note, BindingResult result, @NonNull @PathVariable Long id) {
        if(result.hasFieldErrors()) {
            return this.validation(result);
        }
        Optional<Note> noteOptional = this.noteService.update(id, note);
        if(noteOptional.isPresent()) {
            return ResponseEntity.ok(noteOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable @NonNull Long id) {
        Optional<Note> noteOptional = this.noteService.delete(id);
        if (noteOptional.isPresent()) {
            return ResponseEntity.ok(noteOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Error: " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
