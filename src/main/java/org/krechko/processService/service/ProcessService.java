package org.krechko.processService.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.krechko.processService.exception.ProcessServiceException;
import org.krechko.processService.model.dto.SongDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final RestTemplate restTemplate;
    private static final String FILE_SERVICE_URL = "localhost:7070/api/v1/file/";
    private static final String SONG_SERVICE_URL = "localhost:8080/api/v1";

    @KafkaListener(topics = "song-to-save-topic", groupId = "${group-id}")
    public void listen(String uniqueFileKey) {
        processFile(uniqueFileKey);
    }

    private void processFile(String uniqueFileKey) {
        S3Object s3Object = getFile(uniqueFileKey);
        String artist = getMetadata(s3Object, "artist");
        String title = getMetadata(s3Object, "title");

        restTemplate.postForEntity(SONG_SERVICE_URL, collectMetadataToDTO(artist, title), SongDTO.class);
    }

    private S3Object getFile(String key) {
        return restTemplate.getForObject(FILE_SERVICE_URL + key, S3Object.class);
    }

    private String getMetadata(S3Object s3Object, String tag) {
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            byte[] bytes = inputStream.readAllBytes();
            return extractDataFromMp3(bytes, tag);
        } catch (Exception exception) {
            throw new ProcessServiceException("Failed to get metadata", exception);
        }
    }

    private String extractDataFromMp3(byte[] bytes, String tag) {
        Tika tika = new Tika();
        Metadata metadata = new Metadata();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            tika.parse(inputStream, metadata);
            return metadata.get(tag);
        } catch (Exception exception) {
            throw new ProcessServiceException("Failed to parse file metadata", exception);
        }
    }

    private SongDTO collectMetadataToDTO(String author, String title) {
        return new SongDTO(author, title);
    }
}
