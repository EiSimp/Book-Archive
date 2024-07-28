package group.project.bookarchive.services;

import group.project.bookarchive.models.Messages;
import group.project.bookarchive.repositories.MessageRepository;
import group.project.bookarchive.repositories.BookClubRepository;
import group.project.bookarchive.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    // Sends a message in a book club
    public void sendMessage(Long bookClubId, Long userId, String content) {
        // Find the book club by ID
        var bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new RuntimeException("Book club not found"));
        // Find the user by ID
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Creates a new Message
        var message = new Messages();
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        message.setBookClub(bookClub);
        message.setSender(user);
        // Saves the message to the repository
        messageRepository.save(message);
    }
    // Retrieves all message for a specific book club
    public List<Messages> getMessagesByBookClubId(Long bookClubId) {
        return messageRepository.findByBookClubId(bookClubId);
    }
    // Editing
    public void editMessage(Long messageId, Long userId, String newContent) {
        // Find message by ID
        var message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        // Ensure user is the sender of the message
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own messages");
        }
        message.setContent(newContent);
        messageRepository.save(message);
    }
    //Delete message
    public void deleteMessage(Long messageId, Long userId) {
        //Find message by ID
        var message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        // Ensure user is the sender of the message
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own messages");
        }
        // Deletes the message
        messageRepository.delete(message);
    }
    // Searches for messages by content within a specific book club
    public List<Messages> searchMessages(Long bookClubId, String content) {
        return messageRepository.findByBookClubIdAndContentContaining(bookClubId, content);
    }
}
