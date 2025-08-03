package org.tilenp.dto;

import org.tilenp.enums.ConversationTopic;

public record CreateConversationDTO(ConversationTopic topic, String initialMessage) {
}
