package vn.edu.fpt.dto.request.branch;

import lombok.Data;

@Data
public class BranchEditRequest {
    private String code;  // Mã chi nhánh
    private String name;  // Tên chi nhánh
    private String address;  // Địa chỉ chi nhánh
    private String phone;  // Số điện thoại
    private String email;  // Email chi nhánh
    private Boolean isActive;  // Trạng thái hoạt động
    private String imageUrl;
}