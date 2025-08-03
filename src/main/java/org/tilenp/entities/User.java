package org.tilenp.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.tilenp.enums.UserRole;

@Entity
@Table(name="users")
public class User extends PanacheEntity {
    public String name;
    public UserRole userRole;
    public String password; //TODO: dont save plaintext passwords, lol
}