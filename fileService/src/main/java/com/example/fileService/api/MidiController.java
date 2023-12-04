package com.example.fileService.api;


import com.example.fileService.service.MidiService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("${api.midi.url}")
public class MidiController {
    final private MidiService midiService;

    @Hidden
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body("OK");
    }

    @Operation(
            summary = "Контроллер получения midi файла",
            description = "Возвращает midi файл по данному ID"

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved MIDI file"),
            @ApiResponse(responseCode = "404", description = "MIDI file not found",
                    content = @Content)
    })
    @GetMapping(value = "/{midiId}", produces = "audio/midi")
    public ResponseEntity<byte[]> getMidiById(@PathVariable String midiId) {
        return midiService.sendFileToAsContent(midiId, MediaType.valueOf("audio/midi"));
    }

    @Operation(
            summary = "Контроллер загрузки ногвого midi файла",
            description = "Сохроняет в файловой системе данный midi файл"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully uploaded MIDI file",
                    content = @Content)
    })
    @PostMapping("/uploadFile")
    public ResponseEntity<Object> uploadMidiFile(
            @RequestParam("file") MultipartFile multipartFile)
            throws IOException {
        return midiService.saveFile(multipartFile);
    }

    @Operation(
            summary = "Контроллер удаления midi файла",
            description = "Удаляет midi файл по данному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted MIDI file",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "MIDI file is not found",
                    content = @Content)
    })
    @DeleteMapping("/{midiId}")
    public ResponseEntity<Object> deleteMidiById(@PathVariable("midiId") String midiId) {
        return midiService.deleteFile(midiId);
    }
}