package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String directory) {
        this.root = Paths.get(System.getProperty("user.dir"), directory, BinaryContentStorage.class.getSimpleName());
        if (Files.notExists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);
        try  {
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> download(BinaryContentDto binaryContentDto) {
        System.out.println("binaryContentDto: " + binaryContentDto);
        System.out.println("id: " + binaryContentDto.getId());
        System.out.println("fileName: " + binaryContentDto.getFileName());
        System.out.println("contentType: " + binaryContentDto.getContentType());
        try {
            InputStream inputStream = get(binaryContentDto.getId());
            InputStreamResource  inputStreamResource = new InputStreamResource(inputStream);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(binaryContentDto.getContentType()))
                    .contentLength(binaryContentDto.getSize())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binaryContentDto.getFileName() + "\"")
                    .body(inputStreamResource);
        } catch (Exception e) {
            throw new RuntimeException("download failed", e);
        }
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
