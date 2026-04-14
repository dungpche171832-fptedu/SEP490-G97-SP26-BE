package vn.edu.fpt.dto.response.branch;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchEditResponse {
    private long id;
    private String code;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Boolean isActive;
}