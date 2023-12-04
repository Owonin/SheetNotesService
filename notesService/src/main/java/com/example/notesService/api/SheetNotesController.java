package com.example.notesService.api;

import com.example.notesService.dto.NoteDto;
import com.example.notesService.dto.NotesListDto;
import com.example.notesService.entity.NoteEntity;
import com.example.notesService.services.SheetNoteServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("${api.url}")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SheetNotesController {

    private final SheetNoteServiceImpl sheetNoteService;

    @Operation(summary = "Контролер получения списка нотных записей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение нотной записи",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotesListDto.class)
                    )}),
            @ApiResponse(responseCode = "404", description = "Данные не найдены",
                    content = @Content)
    })
    @GetMapping("/sheetNote")
    public ResponseEntity<?> getAllNotes(
            @Parameter(description = "Номер страницы")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество элементов")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Параметр сортировки и ее порядок (убывание или возрастание)")
            @RequestParam(defaultValue = "id,desc") String[] sort) {
        return ResponseEntity.ok(sheetNoteService.getAllSheetNotes(page, size, sort));
    }

    @Operation(summary = "Контролер получения нотной записи по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение нотной записи по id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NoteEntity.class)
                    )}),
            @ApiResponse(responseCode = "404", description = "Данных по данному id не найдено",
                    content = @Content)
    })
    @GetMapping("/sheetNote/{sheetNoteId}")
    public ResponseEntity<NoteEntity> getNoteEntity(@PathVariable String sheetNoteId) {
        return new ResponseEntity<>(sheetNoteService.getSheetNoteById(sheetNoteId)
                , HttpStatus.OK);
    }

    @Operation(summary = "Контролер создания нотной записи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Id созданной нотной записи",
                    content = @Content)
    }
    )
    @PostMapping("/sheetNote")
    public ResponseEntity<?> createNote(@RequestBody NoteDto noteDto) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var entity = sheetNoteService.createSheetNote(noteDto, auth.getName());

        return ResponseEntity.ok().body(entity);
    }

    @Operation(summary = "Контролер изменения нотной записи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение данных",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Данных по данному id не найдено",
                    content = @Content)
    })
    @PutMapping("/sheetNote")
    public ResponseEntity<?> updateNote(@RequestBody NoteEntity noteEntity) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        sheetNoteService.updateSheetNote(noteEntity, auth);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Контролер удаления нотной записи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение данных",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Данных по данному id не найдено",
                    content = @Content)
    })
    @DeleteMapping("/sheetNote/{sheetNoteId}")
    public ResponseEntity<?> deleteNote(@PathVariable String sheetNoteId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        sheetNoteService.deleteSheetNote(sheetNoteId, auth);

        return ResponseEntity.ok().build();
    }
}
