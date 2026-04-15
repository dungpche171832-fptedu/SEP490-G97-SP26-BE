package vn.edu.fpt.dto.response.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.entity.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private String fullName;
    private String email;
    private String phone;
    private Long roleId;
    private Long branchId;
}