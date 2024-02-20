package com.diego.organizer.springbootorganizer.entities;

import java.util.ArrayList;
import java.util.List;

// import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @ManyToOne
    @JsonIgnoreProperties({"username", "email", "roles", "enabled", "handler", "hibernateLazyInitializer"})
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnoreProperties({"notes", "name", "handler", "hibernateLazyInitializer"})
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "notes_folders", // lista intermedia
        joinColumns = @JoinColumn(name = "note_id"),
        inverseJoinColumns = @JoinColumn(name = "folder_id"),
        uniqueConstraints = {@UniqueConstraint(columnNames = {"note_id", "folder_id"}) }
    )
    private List<Folder> folders;

    private boolean status;

    public Note() {
        this.folders = new ArrayList<>();
    }

    @PrePersist
    public void prePersist() {
        this.status = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolder(List<Folder> folders) {
        this.folders = folders;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    
    
}
