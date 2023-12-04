package com.example.notesService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteDto {

    private String name;

    private String originalAuthor;

    private String midiUrl;

    private String sheetNotePictureUrl;

}
