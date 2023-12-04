package com.example.fileService.api;

import com.example.fileService.service.ImageService;
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

@RequiredArgsConstructor
@RestController
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("${api.image.url}")
public class ImageController {
    final private ImageService imageService;

    @Hidden
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body("OK");
    }

    @Operation(
            summary = "Контроллер скачивания изображения",
            description = "Передает изображение для загрузки пользователем по данному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully send to download Image"),
            @ApiResponse(responseCode = "404", description = "Image is not found",
                    content = @Content)
    })
    @GetMapping(value = "/download/{imageId}", produces = "application/octet-stream")
    public ResponseEntity<?> sendImage(@PathVariable("imageId") String imageId) {
        return imageService.sendFileToDownload(imageId);
    }

    @Operation(
            summary = "Контроллер просмотра изображения",
            description = "Передает изображение для просмотра веб-браузером пользователем по данному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully send Image",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Image is not found",
                    content = @Content)
    })
    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImageById(@PathVariable("imageId") String imageId) {
        return imageService.sendFileToAsContent(imageId, MediaType.IMAGE_PNG);
    }

    @Operation(
            summary = "Контролер передачи изображения в сервис распознования",
            description = "Передает изображение по данному ID генерации midi файла в сервис распознования"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully send to download Image",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Image is not found",
                    content = @Content)
    })
    @PostMapping("/recognise/{imageId}")
    public ResponseEntity<?> recognise(@PathVariable("imageId") String imageId) {
        return imageService.sendImageToRecognitionService(imageId);
    }

    @Operation(
            summary = "Контролер сохранения изображения",
            description = "Сохраняет в файловой системе изображение по данному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Image",
            content = @Content)
    })
    @PostMapping("/uploadFile")
    public ResponseEntity<Object> uploadFile(
            @RequestParam("file") MultipartFile multipartFile)
            throws IOException {
        return imageService.saveFile(multipartFile);
    }

    @Operation(
            summary = "Контролер удаления изображения",
            description = "Удаляет в файловой системе изображение по данному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted image",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Image is not found",
                    content = @Content)
    })
    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<Object> deleteImage(@PathVariable("imageId") String imageId) {
        return imageService.deleteFile(imageId);
    }
}