package com.diego.organizer.springbootorganizer.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JsonIgnoreProperties({"username", "email", "roles", "enabled", "handler", "hibernateLazyInitializer"})
    @JoinColumn(name = "user_id")
    private User user;

    // @JsonIgnoreProperties({"folder", "handler", "hibernateLazyInitializer"})
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL) // to do: agregar la posibilidad de agregar notas a varias carpetas, por ahora se deja cascade
    @JoinTable(
        name = "notes_folders", // lista intermedia
        joinColumns = @JoinColumn(name = "folder_id"),
        inverseJoinColumns = @JoinColumn(name = "note_id"),
        uniqueConstraints = {@UniqueConstraint(columnNames = {"note_id", "folder_id"}) }
    )
    private List<Note> notes;

    public Folder() {
        this.notes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
    
    
}
