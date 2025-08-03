package org.tilenp.dto;

public class CreateMessageDTO {
    private String text;

    public CreateMessageDTO(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
