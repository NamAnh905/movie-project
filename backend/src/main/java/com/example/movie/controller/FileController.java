// src/main/java/com/example/movie/controller/FileController.java
package com.example.movie.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final String UPLOAD_DIR = "uploads"; // thư mục ngay cạnh jar

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Empty file"));
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(Map.of("error", "Only image/* allowed"));
        }

        Files.createDirectories(Paths.get(UPLOAD_DIR));
        String cleanName = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("[^a-zA-Z0-9._-]", "_");
        String fileName = System.currentTimeMillis() + "_" + cleanName;
        Path dest = Paths.get(UPLOAD_DIR, fileName);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }

        String url = "/uploads/" + fileName;           // URL public FE dùng
        return ResponseEntity.ok(Map.of("url", url));  // { "url": "/uploads/..." }
    }
}
