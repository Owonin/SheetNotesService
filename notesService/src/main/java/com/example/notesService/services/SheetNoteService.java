package com.example.notesService.services;

import com.example.notesService.dto.NoteDto;
import com.example.notesService.dto.NotesListDto;
import com.example.notesService.entity.NoteEntity;
import org.springframework.security.core.Authentication;

public interface SheetNoteService {

     NotesListDto getAllSheetNotes(int pageNumber, int pageSize, String[] sort);

     NoteEntity getSheetNoteById(String Id);

     NoteEntity createSheetNote(NoteDto noteDto, String creatorName);

     NoteEntity updateSheetNote(NoteEntity noteEntity, Authentication auth);

     void deleteSheetNote(String id, Authentication auth);

}
