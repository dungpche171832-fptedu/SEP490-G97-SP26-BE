package vn.edu.fpt.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private Long id;
        private String username;
        private String fullName;
        private String role;
        private Long branchId;
    }
}
