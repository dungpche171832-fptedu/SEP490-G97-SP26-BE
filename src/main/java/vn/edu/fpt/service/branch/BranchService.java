package vn.edu.fpt.service.branch;

import vn.edu.fpt.dto.request.branch.AddBranchRequest;
import vn.edu.fpt.dto.request.branch.BranchEditRequest;
import vn.edu.fpt.dto.response.branch.BranchEditResponse;
import vn.edu.fpt.dto.response.branch.BranchViewResponse;
import vn.edu.fpt.dto.response.branch.AddBranchResponse;
import vn.edu.fpt.entity.Branch;

import java.util.List;

public interface BranchService {

    // Phương thức lấy tất cả chi nhánh
    List<Branch> getBranches(String code, String name);

    // Tạo chi nhánh
    AddBranchResponse addBranch(AddBranchRequest request);

    // Xem chi tiết chi nhánh
    BranchViewResponse getBranchDetail(Long id);

    // Sửa chi nhánh
    BranchEditResponse editBranch(Long branchId, BranchEditRequest request);
}