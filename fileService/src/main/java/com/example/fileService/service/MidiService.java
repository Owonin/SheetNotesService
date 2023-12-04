package com.example.fileService.service;

import com.example.fileService.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Service
public class MidiService extends FileServiceImpl{


    public MidiService(@Value("${api.midi.download}") String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void saveMidiFileFromKafkaMessage(String base64EncodedMassage, String fileId){

        byte[] midiBytes = Base64.getDecoder().decode(base64EncodedMassage);

        try {
            FileUploadUtil.saveFile(fileId,midiBytes,fileUrl);
        } catch (IOException e) {
            log.error("Error while saving file {} from kafka massage", fileId);
            throw new RuntimeException(e);
        }

    }
}
