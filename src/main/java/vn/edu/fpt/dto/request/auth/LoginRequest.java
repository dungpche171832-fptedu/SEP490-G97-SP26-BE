package vn.edu.fpt.dto.request.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String email;
    private String password;
}
