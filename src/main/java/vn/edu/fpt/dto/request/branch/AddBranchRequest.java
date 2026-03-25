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
    private Long managerAccountId;
}