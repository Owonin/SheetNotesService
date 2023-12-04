package com.example.notesService.mapper;

import com.example.notesService.dto.NoteDto;
import com.example.notesService.dto.NotesListDto;
import com.example.notesService.entity.NoteEntity;
import org.springframework.data.domain.Page;

public class NoteMapper {
    public static NoteEntity noteDtoToNoteEntity(NoteDto dto){
        var entity = new NoteEntity();
        entity.setMidiUrl(dto.getMidiUrl());
        entity.setSheetNotePictureUrl(dto.getSheetNotePictureUrl());
        entity.setName(dto.getName());
        entity.setOriginalAuthor(dto.getOriginalAuthor());

        return entity;
    }

    public static NotesListDto NotesEntityListToNotesDtoList(Page<NoteEntity> notes) {
        return NotesListDto.builder()
                .notes(notes.getContent())
                .currentPage(notes.getNumber())
                .totalItems(notes.getTotalElements())
                .totalPages(notes.getTotalPages())
                .build();
    }

    public static NoteDto noteEntityToNoteDto(NoteEntity entity){
        return NoteDto.builder()
                .midiUrl(entity.getMidiUrl())
                .name(entity.getName())
                .originalAuthor(entity.getOriginalAuthor())
                .sheetNotePictureUrl(entity.getSheetNotePictureUrl())
                .build();
    }
}
