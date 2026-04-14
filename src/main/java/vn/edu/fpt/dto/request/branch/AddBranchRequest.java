package vn.edu.fpt.dto.request.branch;

import lombok.Data;

@Data
public class AddBranchRequest {
    private String code;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Boolean isActive;

    // Thêm thông tin cho Account
    private String managerFullName;
    private String managerEmail;
    private String managerPhone;
    private String managerPassword;
    private Long roleId;  // Role cho account quản lý
}