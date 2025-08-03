package org.tilenp.dto;

import org.tilenp.entities.Message;

public class MessageDTO {
    private Long id;
    private Long conversationId;
    private UserDTO author;
    private String text;
    private java.time.Instant createdAt;

    public MessageDTO() {}

    public MessageDTO(Long id, Long conversationId, UserDTO author, String text, java.time.Instant createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    public static MessageDTO fromEntity(Message message) {
        if (message == null) {
            return null;
        }
        return new MessageDTO(
            message.id,
            message.conversation.id,
            UserDTO.fromEntity(message.author),
            message.text,
            message.createdAt
        );
    }

    public Long getId() {
        return id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public java.time.Instant getCreatedAt() {
        return createdAt;
    }
}
