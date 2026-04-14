package vn.edu.fpt.service.branch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.branch.AddBranchRequest;
import vn.edu.fpt.dto.response.branch.BranchViewResponse;
import vn.edu.fpt.dto.response.branch.AddBranchResponse;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.entity.Branch;
import vn.edu.fpt.entity.Role;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.repository.BranchRepository;
import vn.edu.fpt.repository.RoleRepository;
import vn.edu.fpt.ultis.errorCode.AccountErrorCode;
import vn.edu.fpt.ultis.errorCode.BranchErrorCode;

import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    public BranchServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;  // Inject BCryptPasswordEncoder
    }

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
        // 1. Kiểm tra mã chi nhánh đã tồn tại hay chưa
        if (branchRepository.existsByCode(request.getCode())) {
            throw new AppException(BranchErrorCode.BRANCH_CODE_ALREADY_EXISTS);
        }
        if (branchRepository.existsByEmail(request.getEmail())) {
            throw new AppException(BranchErrorCode.BRANCH_EMAIL_ALREADY_EXISTS);
        }
        if (branchRepository.existsByPhone(request.getPhone())) {
            throw new AppException(BranchErrorCode.BRANCH_PHONE_ALREADY_EXISTS);
        }
        if (accountRepository.existsByEmail(request.getManagerEmail())) {
            throw new AppException(AccountErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (accountRepository.existsByPhone(request.getManagerPhone())) {
            throw new AppException(AccountErrorCode.PHONE_ALREADY_EXISTS);
        }

        // 3. Tạo tài khoản quản lý mới và mã hóa mật khẩu
        Account manager = new Account();
        manager.setFullName(request.getManagerFullName());
        manager.setEmail(request.getManagerEmail());
        manager.setPhone(request.getManagerPhone());

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(request.getManagerPassword());
        manager.setPassword(encodedPassword);

        // Lấy role từ DB (có thể là Manager hoặc Admin, tùy yêu cầu)
        Role managerRole = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(AccountErrorCode.ROLE_NOT_FOUND));
        manager.setRole(managerRole);

        Account savedManager = accountRepository.save(manager);  // Lưu tài khoản quản lý

        // 3. Tạo chi nhánh mới và gán quản lý
        Branch branch = new Branch();
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        branch.setManagerAccount(savedManager);

        Branch savedBranch = branchRepository.save(branch);

        // 4. Trả về response với thông tin branch và account quản lý
        return AddBranchResponse.builder()
                .id(savedBranch.getId())
                .code(savedBranch.getCode())
                .name(savedBranch.getName())
                .address(savedBranch.getAddress())
                .phone(savedBranch.getPhone())
                .email(savedBranch.getEmail())
                .isActive(savedBranch.getIsActive())
                .managerAccountId(savedManager.getAccountId())  // Id của account quản lý
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