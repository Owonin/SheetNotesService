package com.example.notesService.repository;

import com.example.notesService.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SheetNotesRepository extends JpaRepository<NoteEntity, String> {
}
