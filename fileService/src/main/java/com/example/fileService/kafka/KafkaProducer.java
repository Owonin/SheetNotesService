package com.example.fileService.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    @Value("${topics.send}")
    private String sendClientTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendImageToRecognitionService(String id, String message) {
        log.info("Sending data to recognition service image with id {}" +
                ", payload message length: {}", id, message.length());

        kafkaTemplate.send(sendClientTopic, id, message);
    }

}