package org.tilenp.dto;

public class MessageDTO {
    public Long id;
    public Long conversationId;
    public Long authorId;
    public String authorName;
    public String text;

    public MessageDTO() {}

    public MessageDTO(Long id, Long conversationId, Long authorId, String authorName, String text) {
        this.id = id;
        this.conversationId = conversationId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.text = text;
    }
}
