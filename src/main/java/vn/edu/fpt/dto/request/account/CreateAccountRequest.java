package vn.edu.fpt.dto.request.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String roleName; // ADMIN | MANAGER | STAFF
    private Long branchId;
}