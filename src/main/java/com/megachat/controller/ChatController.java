package com.megachat.controller;

import com.megachat.model.ChatMessage;
import com.megachat.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.megachat.service.FileStorageService;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final FileStorageService fileStorageService;

    public ChatController(ChatMessageService chatMessageService,
                         FileStorageService fileStorageService) {
        this.chatMessageService = chatMessageService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<?> getConversation(@PathVariable Long friendId,
                                             @RequestParam(required = false) Long afterId,
                                             HttpSession session) {
        try {
            Long userId = getUserId(session);
            List<Map<String, Object>> messages = chatMessageService
                .getConversation(userId, friendId, afterId)
                .stream()
                .map(message -> {
                    Map<String, Object> msgMap = new java.util.HashMap<>();
                    msgMap.put("id", message.getId());
                    msgMap.put("senderId", message.getSender().getId());
                    msgMap.put("receiverId", message.getReceiver().getId());
                    msgMap.put("content", message.getContent());
                    msgMap.put("createdAt", message.getCreatedAt());
                    if (message.getFileUrl() != null) {
                        msgMap.put("fileUrl", message.getFileUrl());
                        msgMap.put("fileName", message.getFileName());
                        msgMap.put("fileType", message.getFileType());
                        msgMap.put("fileSize", message.getFileSize());
                    }
                    return msgMap;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "messages", messages
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{friendId}")
    public ResponseEntity<?> sendMessage(@PathVariable Long friendId,
                                         @RequestBody Map<String, String> body,
                                         HttpSession session) {
        try {
            Long userId = getUserId(session);
            String content = body.get("content");
            ChatMessage message = chatMessageService.sendMessage(userId, friendId, content);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", Map.of(
                    "id", message.getId(),
                    "senderId", message.getSender().getId(),
                    "receiverId", message.getReceiver().getId(),
                    "content", message.getContent(),
                    "createdAt", message.getCreatedAt()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{friendId}/upload")
    public ResponseEntity<?> uploadFile(@PathVariable Long friendId,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "text", required = false) String text,
                                       HttpSession session) {
        try {
            Long userId = getUserId(session);
            
            // Upload file
            String filename = fileStorageService.storeFile(file);
            String fileUrl = "/api/messages/files/" + filename;
            
            // Tạo tin nhắn với file, ưu tiên text nếu có
            String content;
            if (text != null && !text.trim().isEmpty()) {
                content = text.trim();
            } else {
                content = file.getOriginalFilename() != null 
                    ? file.getOriginalFilename() 
                    : "Đã gửi file";
            }
            
            ChatMessage message = chatMessageService.sendMessageWithFile(
                userId, 
                friendId, 
                content,
                fileUrl,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", Map.of(
                    "id", message.getId(),
                    "senderId", message.getSender().getId(),
                    "receiverId", message.getReceiver().getId(),
                    "content", message.getContent(),
                    "fileUrl", message.getFileUrl(),
                    "fileName", message.getFileName(),
                    "fileType", message.getFileType(),
                    "fileSize", message.getFileSize(),
                    "createdAt", message.getCreatedAt()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = fileStorageService.loadFile(filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException e) {
                    // ignore
                }
                
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "inline; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getUserId(HttpSession session) throws Exception {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new Exception("Bạn cần đăng nhập để thực hiện thao tác này");
        }
        return userId;
    }
}

