package org.tilenp.dto;

import org.tilenp.entities.User;

public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String role;

    public UserDTO() {}

    public UserDTO(Long id, String name, String username, String role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
            user.id,
            user.name,
            user.username,
            user.userRole
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
