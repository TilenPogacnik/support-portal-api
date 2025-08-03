package org.tilenp.dto;

import org.tilenp.enums.ConversationStatus;
import org.tilenp.enums.ConversationTopic;
import org.tilenp.entities.Conversation;

public class ConversationDTO {
    private Long id;
    private UserDTO customer;
    private UserDTO operator;
    private UserDTO closedBy;
    private ConversationTopic topic;
    private ConversationStatus status;
    private java.time.Instant createdAt;
    private java.time.Instant closedAt;

    public ConversationDTO() {}

    public ConversationDTO(Long id, UserDTO customer, UserDTO operator, UserDTO closedBy, ConversationTopic topic, ConversationStatus status, java.time.Instant createdAt, java.time.Instant closedAt) {
        this.id = id;
        this.customer = customer;
        this.operator = operator;
        this.closedBy = closedBy;
        this.topic = topic;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
    }

    public static ConversationDTO fromEntity(Conversation conversation) {
        if (conversation == null) {
            return null;
        }
        return new ConversationDTO(
            conversation.id,
            UserDTO.fromEntity(conversation.customer),
            UserDTO.fromEntity(conversation.operator),
            UserDTO.fromEntity(conversation.closedBy),
            conversation.topic,
            conversation.status,
            conversation.createdAt,
            conversation.closedAt
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

    public UserDTO getClosedBy() {
        return closedBy;
    }

    public ConversationTopic getTopic() {
        return topic;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public java.time.Instant getCreatedAt() {
        return createdAt;
    }

    public java.time.Instant getClosedAt() {
        return closedAt;
    }
}
