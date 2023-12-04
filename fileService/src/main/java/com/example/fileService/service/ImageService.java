package com.example.fileService.service;

import com.example.fileService.kafka.KafkaProducer;
import com.example.fileService.util.FileDownloadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
public class ImageService extends FileServiceImpl {


    public ImageService(KafkaProducer producer, @Value("${api.image.download}") String fileUrl) {
        this.producer = producer;
        this.fileUrl  =  fileUrl;
    }

    final private KafkaProducer producer;

    public ResponseEntity<Object> sendImageToRecognitionService(String fileId) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();

        log.info("Sending image with id: {} to recognition service", fileId);

        Resource resource = null;
        try {
            resource = downloadUtil.getFileAsResource(fileId, fileUrl);
        } catch (IOException e) {
            log.error("Error occur while getting file for image with id: {}.\n{}", fileId, e.getMessage());

            return ResponseEntity.internalServerError().build();
        }

        byte[] content;
        try {
            content = resource.getContentAsByteArray();
        } catch (IOException e) {
            log.error("File {} cannot be red", resource.getFilename());

            throw new RuntimeException(e);
        }

        String encodedContent = Base64.getEncoder().encodeToString(content);
        producer.sendImageToRecognitionService(fileId, encodedContent);

        return ResponseEntity.ok().build();
    }

}
