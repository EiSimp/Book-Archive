package group.project.bookarchive.controllers;

import group.project.bookarchive.models.ChatMessageDTO;
import group.project.bookarchive.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessageDTO chatMessageDTO) {
        if ("create".equals(chatMessageDTO.getAction())) {
            ChatMessageDTO savedMessage = messageService.sendMessage(
                    chatMessageDTO.getBookClubId(),
                    chatMessageDTO.getUserId(),
                    chatMessageDTO.getContent()
            );
            chatMessageDTO.setMessageId(savedMessage.getMessageId());
            chatMessageDTO.setSentAt(savedMessage.getSentAt());
            chatMessageDTO.setUsername(savedMessage.getUsername());
        }
        messagingTemplate.convertAndSend(
                "/topic/messages/" + chatMessageDTO.getBookClubId(),
                chatMessageDTO
        );
    }

    @MessageMapping("/editMessage")
    public void editMessage(ChatMessageDTO chatMessageDTO) {
        messageService.editMessage(
                chatMessageDTO.getMessageId(),
                chatMessageDTO.getUserId(),
                chatMessageDTO.getContent()
        );
        chatMessageDTO.setAction("edit");
        messagingTemplate.convertAndSend(
                "/topic/messages/" + chatMessageDTO.getBookClubId(),
                chatMessageDTO
        );
    }

    @MessageMapping("/deleteMessage")
    public void deleteMessage(ChatMessageDTO chatMessageDTO) {
        messageService.deleteMessage(
                chatMessageDTO.getMessageId(),
                chatMessageDTO.getUserId()
        );
        chatMessageDTO.setAction("delete");
        messagingTemplate.convertAndSend(
                "/topic/messages/" + chatMessageDTO.getBookClubId(),
                chatMessageDTO
        );
    }
}
