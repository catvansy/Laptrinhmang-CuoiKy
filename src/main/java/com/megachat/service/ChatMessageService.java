package com.megachat.service;

import com.megachat.model.ChatMessage;
import com.megachat.model.User;
import com.megachat.repository.ChatMessageRepository;
import com.megachat.repository.FriendshipRepository;
import com.megachat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private User getUserOrThrow(Long id) throws Exception {
        return userRepository.findById(id)
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng"));
    }
}

