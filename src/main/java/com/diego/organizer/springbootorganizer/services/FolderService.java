package com.diego.organizer.springbootorganizer.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diego.organizer.springbootorganizer.entities.Folder;
import com.diego.organizer.springbootorganizer.repositories.FolderRepository;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Transactional(readOnly = true)
    public List<Folder> findAll() {
        return (List<Folder>)this.folderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Folder> findAllByUserId(Long userId) {
        return (List<Folder>)this.folderRepository.findAllByUserId(userId);
    }

    @Transactional
    public Folder save(@NonNull Folder folder) {
        return this.folderRepository.save(folder);
    }

    @Transactional
    public Optional<Folder> update(@NonNull Long id, Folder folder) {
        Optional<Folder> folderOptional = this.folderRepository.findById(id);
        if(folderOptional.isPresent()) {
            Folder folderDb = folderOptional.orElseThrow();

            folderDb.setName(folder.getName());
            
            return Optional.of(this.folderRepository.save(folderDb)); //save devuelve un objeto de tipo Folder, por eso se usa Optional.of
        }
        return folderOptional;
    }

    @Transactional
    public Optional<Folder> delete(@NonNull Long id) {
        Optional<Folder> folderOptional = this.folderRepository.findById(id);
        folderOptional.ifPresent(folderDb -> {
            if(folderDb != null){
                this.folderRepository.delete(folderDb);
            }
        });
        return folderOptional;
    }
}
