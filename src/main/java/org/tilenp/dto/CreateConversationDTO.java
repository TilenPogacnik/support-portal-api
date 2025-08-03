package org.tilenp.dto;

import org.tilenp.enums.ConversationTopic;

public class CreateConversationDTO {
    public ConversationTopic topic;
    public String initialMessage;

    public CreateConversationDTO(ConversationTopic topic, String initialMessage) {
        this.topic = topic;
        this.initialMessage = initialMessage;
    }
}
