package org.tilenp.dto;

import org.tilenp.entities.Message;

public class MessageDTO {
    private Long id;
    private Long conversationId;
    private UserDTO author;
    private String text;

    public MessageDTO() {}

    public MessageDTO(Long id, Long conversationId, UserDTO author, String text) {
        this.id = id;
        this.conversationId = conversationId;
        this.author = author;
        this.text = text;
    }

    public static MessageDTO fromEntity(Message message) {
        if (message == null) {
            return null;
        }
        return new MessageDTO(
            message.id,
            message.conversation.id,
            UserDTO.fromEntity(message.author),
            message.text
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
}
