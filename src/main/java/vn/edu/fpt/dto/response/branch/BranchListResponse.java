package vn.edu.fpt.dto.response.branch;

import vn.edu.fpt.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BranchListResponse {
    private List<Branch> branches;  // Danh sách các chi nhánh
    private String message;  // Thông báo trả về
    private int totalCount;  // Tổng số chi nhánh
}