package com.example.letmecookbe.controller;

import com.example.letmecookbe.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.uploadFile(file);
        return ResponseEntity.ok(url);
    }
}