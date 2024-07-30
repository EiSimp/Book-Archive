package group.project.bookarchive.controllers;

import group.project.bookarchive.models.Messages;
import group.project.bookarchive.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public void sendMessage(@RequestParam Long bookClubId, @RequestParam Long userId, @RequestParam String content) {
        messageService.sendMessage(bookClubId, userId, content);
    }

    @GetMapping("/bookclub/{bookClubId}")
    public List<Messages> getMessages(@PathVariable Long bookClubId) {
        return messageService.getMessagesByBookClubId(bookClubId);
    }

    @PutMapping("/edit/{messageId}")
    public void editMessage(@PathVariable Long messageId, @RequestParam Long userId, @RequestParam String newContent) {
        messageService.editMessage(messageId, userId, newContent);
    }

    @DeleteMapping("/delete/{messageId}")
    public void deleteMessage(@PathVariable Long messageId, @RequestParam Long userId) {
        messageService.deleteMessage(messageId, userId);
    }

    // Endpoint to search for messages by content within a specific book club
    @GetMapping("/search")
    public List<Messages> searchMessages(@RequestParam Long bookClubId, @RequestParam String content) {
        return messageService.searchMessages(bookClubId, content);
    }
}
