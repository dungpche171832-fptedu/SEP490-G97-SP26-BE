package vn.edu.fpt.service.branch;

import vn.edu.fpt.entity.Branch;

import java.util.List;

public interface BranchService {

    // Phương thức lấy tất cả chi nhánh
    List<Branch> getAllBranches();
}