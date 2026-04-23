package com.giapha.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final Path root = Paths.get("uploads");

    public FileController() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            String extension = "";
            String fileName = file.getOriginalFilename();
            if (fileName != null && fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf("."));
            }
            
            String newFileName = UUID.randomUUID().toString() + extension;
            Path targetPath = this.root.resolve(newFileName);
            
            java.nio.file.Files.copy(file.getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            return ResponseEntity.ok(Map.of("url", "/api/files/download/" + newFileName));
        } catch (Exception e) {
            e.printStackTrace(); // Log to console for debugging
            return ResponseEntity.status(500).body(Map.of("error", "Could not upload the file: " + e.getMessage()));
        }
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        try {
            Path file = root.resolve(filename);
            byte[] data = Files.readAllBytes(file);
            String contentType = Files.probeContentType(file);
            return ResponseEntity.ok()
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
