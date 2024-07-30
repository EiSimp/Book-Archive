package group.project.bookarchive.models;

public class ChatMessageDTO {
    private Long messageId;
    private String content;
    private String sentAt;
    private Long bookClubId;
    private Long userId;
    private String username;  // Added username field
    private String action;

    // Getters and setters
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public Long getBookClubId() {
        return bookClubId;
    }

    public void setBookClubId(Long bookClubId) {
        this.bookClubId = bookClubId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
