package com.example.notesService.dto;

import com.example.notesService.entity.NoteEntity;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class NotesListDto implements Serializable {
    private List<NoteEntity> notes;
    private int currentPage;
    private long totalItems;
    private int totalPages;
}
