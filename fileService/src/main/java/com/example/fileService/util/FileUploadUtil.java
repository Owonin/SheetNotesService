package com.example.fileService.util;


import com.example.fileService.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
public class FileUploadUtil {

    public static String saveFile(String fileName, MultipartFile multipartFile, String fileUrl)
            throws IOException {
        Path uploadPath = Paths.get(fileUrl);

        if (!Files.exists(uploadPath)) {
            log.info("Directory {} is not exists, creating new one",uploadPath);
            Files.createDirectories(uploadPath);
        }

        String fileCode = UUID.randomUUID().toString().substring(0,7);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileCode+".png");
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            String errorMessage = "Could not save file: " + fileName + " " + e.getCause();
            log.error(errorMessage);
            throw new FileException(errorMessage);
        }

        return fileCode;
    }

    public static String saveFile(String fileId, byte[] fileByteArray, String fileUrl)
            throws IOException {
        Path uploadPath = Paths.get(fileUrl);

        if (!Files.exists(uploadPath)) {
            log.info("Directory {} is not exists, creating new one",uploadPath);
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = new ByteArrayInputStream(fileByteArray)) {
            Path filePath = uploadPath.resolve(fileId+".mid");
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            String errorMessage = "Could not save file: " + fileId + " "+ e.getCause();
            log.error(errorMessage);
            throw new FileException(errorMessage);
        }

        return fileId;
    }
}