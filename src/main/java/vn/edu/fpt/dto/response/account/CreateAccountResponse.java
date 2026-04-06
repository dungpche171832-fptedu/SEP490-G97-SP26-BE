package vn.edu.fpt.dto.response.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder

public class CreateAccountResponse {
    private Long accountId;
    private String fullName;
    private String email;
    private String role;
    private Long branchId;
}
