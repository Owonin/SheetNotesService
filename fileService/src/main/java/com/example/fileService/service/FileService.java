package com.example.fileService.service;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
     ResponseEntity<Object> sendFileToDownload(String fileId);
     ResponseEntity<Object> saveFile(MultipartFile file);
     ResponseEntity<byte[]> sendFileToAsContent(String fileId, MediaType mediaType);
     ResponseEntity<Object> deleteFile(String fileId);
}
