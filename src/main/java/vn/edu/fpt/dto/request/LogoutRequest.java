package vn.edu.fpt.dto.request;

import lombok.*;

@Data
public class LogoutRequest {
    private String refreshToken;
}

