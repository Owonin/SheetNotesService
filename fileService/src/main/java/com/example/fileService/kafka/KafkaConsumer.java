package com.example.fileService.kafka;

import com.example.fileService.service.MidiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {
    final private MidiService midiService;

    @KafkaListener(topics = {"${spring.kafka.listener.topics.midi}"})
    public void handleKafkaMessage(ConsumerRecord<String, String> message) {
        log.info("Got new midi file from recognition service with key {}", message.key());

        midiService.saveMidiFileFromKafkaMessage(message.value(), message.key());
    }

}
