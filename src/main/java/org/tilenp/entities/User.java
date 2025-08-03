package org.tilenp.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;

@Entity
@Table(name="users")
@UserDefinition
public class User extends PanacheEntity {
    public String name;

    @Username
    public String username;

    @Password
    public String password;
    
    @Roles
    public String userRole;
}