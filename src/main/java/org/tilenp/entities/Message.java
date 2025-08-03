package org.tilenp.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;


@Entity
@Table(name="messages")
public class Message extends PanacheEntity {
    //TODO: message timestamp
    @ManyToOne
    @JoinColumn(name="conversation_id", nullable = false)
    public Conversation conversation;

    @ManyToOne
    @JoinColumn(name="author_id", nullable = false)
    public User author;

    @Column(nullable = false)
    public String text;
}
