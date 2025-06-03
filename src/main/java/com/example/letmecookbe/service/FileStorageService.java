package com.example.letmecookbe.service;

import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload-dir:uploads/}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080/upload/}")
    private String baseUrl;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 10MB limit

    /**
     * Upload a file to the server and return its URL.
     *
     * @param file The multipart file to upload
     * @return The URL of the uploaded file
     * @throws AppException if upload fails or file is invalid
     */

    public String uploadFile(MultipartFile file) {
        validateFile(file);

        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir + fileName);
            Files.createDirectories(uploadPath.getParent());
            Files.write(uploadPath, file.getBytes());
            log.info("File uploaded successfully: {}", fileName);
            return baseUrl + fileName;
        } catch (IOException e) {
            log.error("Upload failed: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Delete a file from the server.
     *
     * @param fileUrl The URL of the file to delete
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        try {
            // Extract the file name from the URL (e.g., http://localhost:8088/upload/filename.png -> filename.png)
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir + fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted successfully: {}", fileName);
            } else {
                log.warn("File not found for deletion: {}", fileName);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", e.getMessage());
            throw new AppException(ErrorCode.DELETE_FILE_FAILED);
        }
    }

    /**
     * Validate the file before uploading.
     *
     * @param file The multipart file to validate
     * @throws AppException if validation fails
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    public byte[] serveFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir + fileName);
            if (!Files.exists(filePath)) {
                log.warn("File not found: {}", fileName);
                throw new AppException(ErrorCode.FILE_NOT_FOUND);
            }

            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to serve file: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_READ_FAILED);
        }
    }
}

