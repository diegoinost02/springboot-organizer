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

import com.diego.organizer.springbootorganizer.entities.Folder;
import com.diego.organizer.springbootorganizer.services.FolderService;

import jakarta.validation.Valid;

@CrossOrigin(origins = {"http://localhost:4200"}) 
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @GetMapping
    public List<Folder> list() {
        return this.folderService.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Folder> listByUserId(@PathVariable Long userId) {
        return this.folderService.findAllByUserId(userId);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody @NonNull Folder folder, BindingResult result) {
        if(result.hasFieldErrors()){
            return this.validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.folderService.save(folder));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Folder folder, BindingResult result, @NonNull @PathVariable Long id) {
        if(result.hasFieldErrors()) {
            return this.validation(result);
        }
        Optional<Folder> folderOptional = this.folderService.update(id, folder);
        if(folderOptional.isPresent()) {
            return ResponseEntity.ok(folderOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable @NonNull Long id) {
        Optional<Folder> folderOptional = this.folderService.delete(id);
        if(folderOptional.isPresent()) {
            return ResponseEntity.ok(folderOptional.orElseThrow());
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
