package vn.edu.fpt.service.branch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.branch.AddBranchRequest;
import vn.edu.fpt.dto.response.branch.BranchViewResponse;
import vn.edu.fpt.dto.response.branch.AddBranchResponse;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.entity.Branch;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.repository.BranchRepository;
import vn.edu.fpt.ultis.errorCode.BranchErrorCode;

import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Branch> getBranches(String code, String name) {

        List<Branch> branches;

        // không truyền gì → get all
        if ((code == null || code.isBlank()) && (name == null || name.isBlank())) {
            branches = branchRepository.findAll();
        } else {
            branches = branchRepository.filterBranches(code, name);
        }

        if (branches.isEmpty()) {
            throw new AppException(BranchErrorCode.BRANCH_LIST_EMPTY);
        }

        return branches;
    }

    @Override
    @Transactional
    public AddBranchResponse addBranch(AddBranchRequest request) {

        // 1. Duplicate code
        if (branchRepository.existsByCode(request.getCode())) {
            throw new AppException(BranchErrorCode.BRANCH_CODE_ALREADY_EXISTS);
        }

        // 2. Manager not found
        Account manager = accountRepository.findById(request.getManagerAccountId())
                .orElseThrow(() -> new AppException(BranchErrorCode.MANAGER_ACCOUNT_NOT_FOUND));

        // 3. Map entity
        Branch branch = new Branch();
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        branch.setManagerAccount(manager);

        Branch saved = branchRepository.save(branch);

        // 4. Response
        return AddBranchResponse.builder()
                .id(saved.getId())
                .code(saved.getCode())
                .name(saved.getName())
                .address(saved.getAddress())
                .phone(saved.getPhone())
                .email(saved.getEmail())
                .isActive(saved.getIsActive())
                .managerAccountId(saved.getManagerAccount().getAccountId())
                .build();
    }

    @Override
    public BranchViewResponse getBranchDetail(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new AppException(BranchErrorCode.BRANCH_NOT_FOUND));

        if (!Boolean.TRUE.equals(branch.getIsActive())) {
            throw new AppException(BranchErrorCode.BRANCH_NOT_ACTIVE);
        }

        return BranchViewResponse.builder()
                .id(branch.getId())
                .code(branch.getCode())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .email(branch.getEmail())
                .isActive(branch.getIsActive())
                .managerId(branch.getManagerAccount().getAccountId())
                .managerName(branch.getManagerAccount().getFullName())
                .build();
    }
}