package org.tilenp.dto;

public class CreateMessageDTO {
    public Long authorId;
    public String text;

    public CreateMessageDTO(Long authorId, String text) {
        this.authorId = authorId;
        this.text = text;
    }
}
