package com.example.notesService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "sheetNotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "NoteSheetName")
    private String name;

    @Column(name = "OriginalAuthor")
    private String originalAuthor;

    @Column(name= "SheetNoteCreator")
    private String sheetNoteCreator;

    @Column(name= "CreationTimestamp")
    private LocalDate creationTimestamp;

    @Column(name = "MidiUrl")
    private String midiUrl;

    @Column(name = "SheetNotePictureUrl")
    private String sheetNotePictureUrl;

}
