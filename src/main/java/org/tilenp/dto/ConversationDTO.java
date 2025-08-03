package org.tilenp.dto;

import org.tilenp.enums.ConversationStatus;
import org.tilenp.enums.ConversationTopic;

public class ConversationDTO {
    public Long id;
    public Long customerId; //use UserDTO instead of separate fields
    public String customerName;
    public Long operatorId;
    public String operatorName;
    public ConversationTopic topic;
    public ConversationStatus status;

    public ConversationDTO() {}

    public ConversationDTO(Long id, Long customerId, String customerName, Long operatorId, String operatorName, ConversationTopic topic, ConversationStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.topic = topic;
        this.status = status;
    }
}
