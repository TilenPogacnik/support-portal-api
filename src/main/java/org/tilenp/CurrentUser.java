package org.tilenp;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import io.quarkus.security.identity.SecurityIdentity;
import org.tilenp.entities.User;
import org.tilenp.exception.ErrorMessages;

@RequestScoped
public class CurrentUser {

    @Inject
    SecurityIdentity securityIdentity;

    private User user;

    public User get() {
        if (user == null) {
            if (securityIdentity == null || securityIdentity.getPrincipal() == null) {
                throw new NotAuthorizedException(ErrorMessages.AUTHENTICATION_REQUIRED);
            }
            String username = securityIdentity.getPrincipal().getName();
            user = User.find("username", username).firstResult();
            if (user == null) {
                throw new NotAuthorizedException(String.format(ErrorMessages.USER_NOT_FOUND, username));
            }
        }
        return user;
    }
}
