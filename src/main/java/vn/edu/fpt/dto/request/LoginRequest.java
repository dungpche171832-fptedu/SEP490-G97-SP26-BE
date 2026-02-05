package vn.edu.fpt.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String username;
    private String password;
}
