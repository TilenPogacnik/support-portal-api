package org.tilenp.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.tilenp.enums.ConversationStatus;
import org.tilenp.enums.ConversationTopic;

import java.util.List;

@Entity
@Table(name="conversations")
public class Conversation extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name="customer_id")
    public User customer;

    @ManyToOne
    @JoinColumn(name="operator_id")
    public User operator;

    @Enumerated(EnumType.STRING)
    public ConversationTopic topic;
    
    @Enumerated(EnumType.STRING)
    public ConversationStatus status;

    @OneToMany(mappedBy="conversation")
    public List<Message> messages;

    public static Conversation findById(Long id){ //TODO move to repository
        return find("id", id).firstResult();
    }
}