package org.devberat.service;

import org.devberat.model.User;
import org.devberat.model.UserType;

public interface ISecurityService {
    User getCurrentUser();
    void checkAuthority(UserType... allowedTypes);
    boolean isCurrentUser(User user);
    boolean hasRole(UserType type);
}
