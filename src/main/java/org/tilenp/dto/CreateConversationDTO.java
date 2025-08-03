package org.tilenp.dto;

import org.tilenp.enums.ConversationTopic;

public class CreateConversationDTO {
    public Long customerId;
    public ConversationTopic topic;
    public String initialMessage;

    public CreateConversationDTO(Long customerId, ConversationTopic topic, String initialMessage) {
        this.customerId = customerId;
        this.topic = topic;
        this.initialMessage = initialMessage;
    }
}
