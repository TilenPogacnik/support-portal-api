package org.tilenp;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import io.quarkus.security.identity.SecurityIdentity;
import org.tilenp.entities.User;

@RequestScoped
public class CurrentUser {

    @Inject
    SecurityIdentity securityIdentity;

    private User user;

    public User get() {
        if (user == null) {
            String username = securityIdentity.getPrincipal().getName();
            user = User.find("username", username).firstResult();
            if (user == null) {
                throw new IllegalArgumentException("Authenticated user not found: " + username);
            }
        }
        return user;
    }
}
