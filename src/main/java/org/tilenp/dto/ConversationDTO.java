package org.tilenp.dto;

import org.tilenp.enums.ConversationStatus;
import org.tilenp.enums.ConversationTopic;
import org.tilenp.entities.Conversation;

public class ConversationDTO {
    private Long id;
    private UserDTO customer;
    private UserDTO operator;
    private ConversationTopic topic;
    private ConversationStatus status;

    public ConversationDTO() {}

    public ConversationDTO(Long id, UserDTO customer, UserDTO operator, ConversationTopic topic, ConversationStatus status) {
        this.id = id;
        this.customer = customer;
        this.operator = operator;
        this.topic = topic;
        this.status = status;
    }

    public static ConversationDTO fromEntity(Conversation conversation) {
        if (conversation == null) {
            return null;
        }
        return new ConversationDTO(
            conversation.id,
            UserDTO.fromEntity(conversation.customer),
            UserDTO.fromEntity(conversation.operator),
            conversation.topic,
            conversation.status
        );
    }

    public Long getId() {
        return id;
    }

    public UserDTO getCustomer() {
        return customer;
    }

    public UserDTO getOperator() {
        return operator;
    }

    public ConversationTopic getTopic() {
        return topic;
    }

    public ConversationStatus getStatus() {
        return status;
    }
}
