package org.tilenp.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name="messages")
public class Message extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name="conversation_id", nullable = false)
    public Conversation conversation;

    @ManyToOne //TODO: a je to ok? verjetno ne rabimo joinat? ali pac
    @JoinColumn(name="author_id", nullable = false)
    public User author;
    public String text;

    public static List<Message> findByConversationId(Long conversationId) {
        return find("conversation.id", conversationId).list();
    }
}
