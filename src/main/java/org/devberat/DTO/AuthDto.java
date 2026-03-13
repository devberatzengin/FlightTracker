package org.devberat.DTO;

import lombok.Builder;
import lombok.Data;

public class AuthDto {
    @Data
    public static class Request {
        private String email;
        private String password;
    }

    @Data
    @Builder
    public static class Response {
        private String token;
        private String email;
    }
}