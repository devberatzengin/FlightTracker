package org.devberat.service.Impl;

import org.devberat.exception.BaseException;
import org.devberat.exception.ErrorMessage;
import org.devberat.exception.MessageType;
import org.devberat.model.User;
import org.devberat.model.UserType;
import org.devberat.repository.IUserRepository;
import org.devberat.service.ISecurityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements ISecurityService {

    private final IUserRepository userRepository;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Authentication required!"));
        }
        
        if (authentication.getPrincipal() instanceof User user) {
            return userRepository.findById(user.getId()).orElseThrow();
        }
        
        throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Invalid user session!"));
    }

    @Override
    public void checkAuthority(UserType... allowedTypes) {
        User currentUser = getCurrentUser();
        boolean hasPermission = Arrays.stream(allowedTypes)
                .anyMatch(type -> type.equals(currentUser.getUserType()));
        
        if (!hasPermission) {
            throw new AccessDeniedException("Access Denied: You do not have permission to perform this operation.");
        }
    }

    @Override
    public boolean isCurrentUser(User user) {
        if (user == null) return false;
        return getCurrentUser().getId().equals(user.getId());
    }

    @Override
    public boolean hasRole(UserType type) {
        return getCurrentUser().getUserType().equals(type);
    }
}
