package org.tilenp.dto;

import org.tilenp.enums.ConversationTopic;

public class CreateConversationDTO {
    private ConversationTopic topic;
    private String initialMessage;

    public CreateConversationDTO(ConversationTopic topic, String initialMessage) {
        this.topic = topic;
        this.initialMessage = initialMessage;
    }

    public ConversationTopic getTopic() {
        return topic;
    }

    public String getInitialMessage() {
        return initialMessage;
    }
}
