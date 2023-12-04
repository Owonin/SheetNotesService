package com.example.fileService.service;

import com.example.fileService.responce.FileUploadResponse;
import com.example.fileService.util.FileDownloadUtil;
import com.example.fileService.util.FileUploadUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


@Slf4j
abstract class FileServiceImpl implements FileService {

    protected String fileUrl;

    @Override
    public ResponseEntity<Object> sendFileToDownload(String file) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();

        Resource resource = null;
        try {
            resource = downloadUtil.getFileAsResource(file, fileUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    @Override
    public ResponseEntity<Object> deleteFile(String fileId) {
        String filePath = fileUrl + fileId;

        Resource resource = new ClassPathResource(filePath);

        if (resource.exists()) {
            try {
                Files.delete(resource.getFile().toPath());
            } catch (IOException e) {
                log.error("Error during deleting file bytes from {}", fileId);
                throw new RuntimeException(e);
            }

            return ResponseEntity.ok().build();
        } else {

            return new ResponseEntity<>(null, null, 404);
        }
    }

    @Override
    public ResponseEntity<Object> saveFile(MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        log.info("Saving new file");

        String fileCode = null;
        try {
            fileCode = FileUploadUtil.saveFile(fileName, multipartFile, fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("download/" + fileCode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> sendFileToAsContent(String fileId, MediaType mediaType) {
        Path dirPath = Paths.get(fileUrl);
        File foundFile;
        byte[] fileBytes;

        try {
            foundFile = Files
                    .list(dirPath)
                    .filter(file -> file.getFileName()
                            .toString()
                            .startsWith(fileId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No file found"))
                    .toFile();

            fileBytes = Files.readAllBytes(foundFile.toPath());

        } catch (IOException e) {
            log.error("Error during reading file bytes from {}", fileId);

            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        return new ResponseEntity<>(fileBytes, headers, 200);
    }

}
