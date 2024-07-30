package group.project.bookarchive.controllers;

import group.project.bookarchive.models.ChatMessageDTO;
import group.project.bookarchive.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ChatMessageDTO sendMessage(@RequestParam Long bookClubId, @RequestParam Long userId, @RequestParam String content) {
        return messageService.sendMessage(bookClubId, userId, content);
    }

    @GetMapping("/bookclub/{bookClubId}")
    public List<ChatMessageDTO> getMessages(@PathVariable Long bookClubId) {
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
}
