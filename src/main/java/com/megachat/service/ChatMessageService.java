package com.megachat.service;

import com.megachat.model.ChatMessage;
import com.megachat.model.User;
import com.megachat.repository.ChatMessageRepository;
import com.megachat.repository.FriendshipRepository;
import com.megachat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public ChatMessageService(ChatMessageRepository messageRepository,
                              UserRepository userRepository,
                              FriendshipRepository friendshipRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Transactional
    public ChatMessage sendMessage(Long senderId, Long receiverId, String content) throws Exception {
        if (content == null || content.trim().isEmpty()) {
            throw new Exception("Tin nhắn không được để trống");
        }

        User sender = getUserOrThrow(senderId);
        User receiver = getUserOrThrow(receiverId);

        if (!friendshipRepository.existsAcceptedFriendship(sender, receiver)) {
            throw new Exception("Bạn chỉ có thể nhắn tin với người đã là bạn bè");
        }

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content.trim());

        return messageRepository.save(message);
    }

    @Transactional
    public ChatMessage sendMessageWithFile(Long senderId, Long receiverId, 
                                      String content, String fileUrl,
                                      String fileName, String fileType, Long fileSize) throws Exception {
        User sender = getUserOrThrow(senderId);
        User receiver = getUserOrThrow(receiverId);

        if (!friendshipRepository.existsAcceptedFriendship(sender, receiver)) {
            throw new Exception("Bạn chỉ có thể nhắn tin với người đã là bạn bè");
        }

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content != null ? content.trim() : "");
        message.setFileUrl(fileUrl);
        message.setFileName(fileName);
        message.setFileType(fileType);
        message.setFileSize(fileSize);

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getConversation(Long userId, Long friendId, Long afterId) throws Exception {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        if (!friendshipRepository.existsAcceptedFriendship(user, friend)) {
            throw new Exception("Hai người chưa là bạn bè");
        }

        return messageRepository.findConversation(userId, friendId, afterId);
    }

    @Transactional
    public ChatMessage editMessage(Long messageId, Long userId, String newContent) throws Exception {
        ChatMessage message = messageRepository.findById(messageId)
            .orElseThrow(() -> new Exception("Không tìm thấy tin nhắn"));

        if (!message.getSender().getId().equals(userId)) {
            throw new Exception("Bạn chỉ có thể chỉnh sửa tin nhắn của chính mình");
        }

        if (message.getIsDeleted()) {
            throw new Exception("Không thể chỉnh sửa tin nhắn đã bị xóa");
        }

        if (newContent == null || newContent.trim().isEmpty()) {
            throw new Exception("Nội dung tin nhắn không được để trống");
        }

        message.setContent(newContent.trim());
        message.setEditedAt(java.time.LocalDateTime.now());

        return messageRepository.save(message);
    }

    @Transactional
    public ChatMessage deleteMessage(Long messageId, Long userId) throws Exception {
        ChatMessage message = messageRepository.findById(messageId)
            .orElseThrow(() -> new Exception("Không tìm thấy tin nhắn"));

        if (!message.getSender().getId().equals(userId)) {
            throw new Exception("Bạn chỉ có thể xóa tin nhắn của chính mình");
        }

        message.setIsDeleted(true);
        message.setContent("Tin nhắn đã bị xóa");

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMedia(Long userId, Long friendId) throws Exception {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        if (!friendshipRepository.existsAcceptedFriendship(user, friend)) {
            throw new Exception("Hai người chưa là bạn bè");
        }

        List<ChatMessage> messages = messageRepository.findConversation(userId, friendId, null);
        
        return messages.stream()
            .filter(msg -> msg.getFileUrl() != null && isImageFile(msg.getFileType()))
            .map(msg -> {
                Map<String, Object> mediaMap = new HashMap<>();
                mediaMap.put("id", msg.getId());
                mediaMap.put("fileUrl", msg.getFileUrl());
                mediaMap.put("fileName", msg.getFileName());
                mediaMap.put("fileType", msg.getFileType());
                mediaMap.put("createdAt", msg.getCreatedAt());
                mediaMap.put("senderId", msg.getSender().getId());
                return mediaMap;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFiles(Long userId, Long friendId) throws Exception {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        if (!friendshipRepository.existsAcceptedFriendship(user, friend)) {
            throw new Exception("Hai người chưa là bạn bè");
        }

        List<ChatMessage> messages = messageRepository.findConversation(userId, friendId, null);
        
        return messages.stream()
            .filter(msg -> msg.getFileUrl() != null && !isImageFile(msg.getFileType()))
            .map(msg -> {
                Map<String, Object> fileMap = new HashMap<>();
                fileMap.put("id", msg.getId());
                fileMap.put("fileUrl", msg.getFileUrl());
                fileMap.put("fileName", msg.getFileName());
                fileMap.put("fileType", msg.getFileType());
                fileMap.put("fileSize", msg.getFileSize());
                fileMap.put("createdAt", msg.getCreatedAt());
                fileMap.put("senderId", msg.getSender().getId());
                return fileMap;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLinks(Long userId, Long friendId) throws Exception {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        if (!friendshipRepository.existsAcceptedFriendship(user, friend)) {
            throw new Exception("Hai người chưa là bạn bè");
        }

        List<ChatMessage> messages = messageRepository.findConversation(userId, friendId, null);
        Pattern urlPattern = Pattern.compile(
            "(?i)\\b((?:https?://|www\\.)[\\w\\-]+(?:\\.[\\w\\-]+)+[\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])",
            Pattern.CASE_INSENSITIVE
        );
        
        return messages.stream()
            .filter(msg -> msg.getContent() != null)
            .flatMap(msg -> {
                Matcher matcher = urlPattern.matcher(msg.getContent());
                return matcher.results()
                    .map(matchResult -> {
                        Map<String, Object> linkMap = new HashMap<>();
                        linkMap.put("id", msg.getId());
                        linkMap.put("url", matchResult.group(1));
                        linkMap.put("createdAt", msg.getCreatedAt());
                        linkMap.put("senderId", msg.getSender().getId());
                        return linkMap;
                    });
            })
            .collect(Collectors.toList());
    }

    private boolean isImageFile(String fileType) {
        if (fileType == null) return false;
        return fileType.startsWith("image/");
    }

    private User getUserOrThrow(Long id) throws Exception {
        return userRepository.findById(id)
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng"));
    }
}

