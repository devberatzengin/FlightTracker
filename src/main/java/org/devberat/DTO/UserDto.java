package org.devberat.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.devberat.model.UserType;
import java.util.UUID;

public class UserDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private UserType userType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phoneNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateResponse {
        private UUID id;
        @NotNull(message = "User type cannot be null")
        private String firstName;
        private String lastName;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusChangeRequest {
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusResponse {
        private UUID id;
        private boolean active;
    }
}