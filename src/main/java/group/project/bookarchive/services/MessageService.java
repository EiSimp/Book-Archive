package group.project.bookarchive.services;

import group.project.bookarchive.models.ChatMessageDTO;
import group.project.bookarchive.models.Messages;
import group.project.bookarchive.repositories.BookClubRepository;
import group.project.bookarchive.repositories.MessageRepository;
import group.project.bookarchive.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final BookClubRepository bookClubRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, BookClubRepository bookClubRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.bookClubRepository = bookClubRepository;
        this.userRepository = userRepository;
    }

    public ChatMessageDTO sendMessage(Long bookClubId, Long userId, String content) {
        var bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new RuntimeException("Book club not found"));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var message = new Messages();
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        message.setBookClub(bookClub);
        message.setSender(user);
        message = messageRepository.save(message);

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setMessageId(message.getId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt().toString());
        dto.setBookClubId(message.getBookClub().getId());
        dto.setUserId(message.getSender().getId());
        dto.setUsername(message.getSender().getUsername());
        return dto;
    }

    public List<ChatMessageDTO> getMessagesByBookClubId(Long bookClubId) {
        return messageRepository.findByBookClubId(bookClubId).stream()
                .map(message -> {
                    ChatMessageDTO dto = new ChatMessageDTO();
                    dto.setMessageId(message.getId());
                    dto.setContent(message.getContent());
                    dto.setSentAt(message.getSentAt().toString());
                    dto.setBookClubId(message.getBookClub().getId());
                    dto.setUserId(message.getSender().getId());
                    dto.setUsername(message.getSender().getUsername());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void editMessage(Long messageId, Long userId, String newContent) {
        var message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own messages");
        }
        message.setContent(newContent);
        messageRepository.save(message);
    }

    public void deleteMessage(Long messageId, Long userId) {
        var message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own messages");
        }
        messageRepository.delete(message);
    }

    public List<Messages> searchMessages(Long bookClubId, String content) {
        return messageRepository.findByBookClubIdAndContentContaining(bookClubId, content);
    }
}
