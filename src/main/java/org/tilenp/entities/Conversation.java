package org.tilenp.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.tilenp.enums.ConversationStatus;
import org.tilenp.enums.ConversationTopic;
import org.tilenp.enums.UserRole;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name="conversations")
public class Conversation extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name="customer_id", nullable = false)
    public User customer;

    @ManyToOne
    @JoinColumn(name="operator_id")
    public User operator;

    @ManyToOne
    @JoinColumn(name="closed_by_id")
    public User closedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ConversationTopic topic;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ConversationStatus status;

    @OneToMany(mappedBy="conversation")
    public List<Message> messages;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "closed_at")
    public Instant closedAt;

    public static Conversation findById(Long id){
        return find("id", id).firstResult();
    }

    public static List<Conversation> findConversations(User user, List<Long> includedOperators, List<String> includedTopics, List<String> includedStatuses){
        StringBuilder queryBuilder = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        
        if (UserRole.USER.equals(user.userRole)) {
            queryBuilder.append("customer = :customer");
            params.put("customer", user);
        } else {
            queryBuilder.append("1=1"); // Always true condition for operators
        }
        
        if (includedOperators != null && !includedOperators.isEmpty()) {
            queryBuilder.append(" AND operator.id IN :operatorIds");
            params.put("operatorIds", includedOperators);
        }
        
        if (includedTopics != null && !includedTopics.isEmpty()) {
            queryBuilder.append(" AND UPPER(topic) IN :topics");
            params.put("topics", includedTopics);
        }
        
        if (includedStatuses != null && !includedStatuses.isEmpty()) {
            queryBuilder.append(" AND status IN :statuses");
            params.put("statuses", includedStatuses);
        }
        
        return Conversation.list(queryBuilder.toString(), params);
    }
}