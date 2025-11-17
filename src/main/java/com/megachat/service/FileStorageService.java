package com.megachat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final String UPLOAD_DIR = "uploads";
    private final Path uploadPath;
    
    public FileStorageService() throws IOException {
        this.uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }
    
    public String storeFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("File không được để trống");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new Exception("File không được vượt quá 100MB");
        }
        
        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // Lưu file
        Path targetPath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        return uniqueFilename;
    }
    
    public Path loadFile(String filename) {
        return uploadPath.resolve(filename);
    }
    
    public boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
}
