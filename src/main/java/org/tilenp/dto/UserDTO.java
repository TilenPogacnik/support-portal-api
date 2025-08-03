package org.tilenp.dto;

import org.tilenp.entities.User;

public class UserDTO {
    private String name;
    private String role;

    public UserDTO() {}

    public UserDTO(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
            user.name,
            user.userRole
        );
    }
    
    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
