package vn.edu.fpt.service.branch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.entity.Branch;
import vn.edu.fpt.repository.BranchRepository;


import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepository branchRepository;

    // Triển khai phương thức lấy tất cả chi nhánh
    @Override
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }
}