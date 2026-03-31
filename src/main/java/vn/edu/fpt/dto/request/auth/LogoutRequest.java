package vn.edu.fpt.dto.request.auth;

import lombok.*;

@Data
public class LogoutRequest {
    private String refreshToken;
}

