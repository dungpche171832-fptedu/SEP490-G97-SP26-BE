package vn.edu.fpt.service.account;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.dto.request.account.CreateAccountRequest;
import vn.edu.fpt.dto.request.account.UpdateProfileRequest;
import vn.edu.fpt.dto.request.account.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.dto.response.account.AccountResponse;
import vn.edu.fpt.dto.response.account.CreateAccountResponse;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.entity.Role;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.repository.RoleRepository;
import vn.edu.fpt.ultis.errorCode.AccountErrorCode;


import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Account> getAccounts(List<String> roles, Long branchId, String email) {

        Specification<Account> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and((root, query, cb) ->
                cb.equal(cb.upper(root.get("status")), "ACTIVE")
        );

        // role
        if (roles != null && !roles.isEmpty()) {
            List<String> normalizedRoles = roles.stream()
                    .map(this::normalizeRoleName)
                    .toList();

            spec = spec.and((root, query, cb) ->
                    root.get("role").get("name").in(normalizedRoles)
            );
        }

        // branch
        if (branchId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("branchId"), branchId)
            );
        }

        // email
        if (email != null && !email.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%")
            );
        }

        List<Account> accounts = accountRepository.findAll(spec);

        if (accounts.isEmpty()) {
            throw new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND);
        }

        return accounts;
    }

    private String normalizeRoleName(String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN" -> "Admin";
            case "MANAGER" -> "Manager";
            case "STAFF" -> "Staff";
            case "CUSTOMER" -> "Customer";
            default -> throw new RuntimeException("Invalid role: " + role);
        };
    }

    @Transactional
    public AccountResponse updateProfile(UpdateProfileRequest request) {

        // 1. Lấy email từ token
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String email;

        if (principal instanceof String) {
            email = (String) principal;
        } else {
            throw new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND);
        }

        // 2. Lấy account
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        // 3. Check trùng phone (nếu có nhập)
        if (request.getPhone() != null &&
                accountRepository.existsByPhone(request.getPhone()) &&
                !request.getPhone().equals(account.getPhone())) {

            throw new AppException(AccountErrorCode.PHONE_ALREADY_EXISTS);
        }

        // 4. Update field
        if (request.getFullName() != null) {
            account.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            account.setPhone(request.getPhone());
        }

        accountRepository.save(account);

        // 5. Trả response
        return AccountResponse.builder()
                .fullName(account.getFullName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .build();
    }
    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        // 1. Lấy email từ token
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String email;

        if (principal instanceof String) {
            email = (String) principal;
        } else {
            throw new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND);
        }

        // 2. Lấy account
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        // 3. Check confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(AccountErrorCode.NEW_PASSWORD_CONFIRM_NOT_MATCH);
        }

        // 4. Check mật khẩu mới
        if (!isValidPassword(request.getNewPassword())) {
            throw new AppException(AccountErrorCode.INVALID_PASSWORD);
        }
        // 5. Check mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
            throw new AppException(AccountErrorCode.INVALID_CURRENT_PASSWORD);
        }

        // 6. Check mật khẩu mới không được trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), account.getPassword())) {
            throw new AppException(AccountErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT);
        }

        // 7. Encode mật khẩu mới
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // 8. Save
        accountRepository.save(account);
    }

    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Z]).{8,}$");
    }

    @Override
    @Transactional
    public CreateAccountResponse createAccount(CreateAccountRequest request) {
// 0. Lấy user hiện tại (dùng để kiểm tra quyền)
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Account currentUser = accountRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        String currentRole = currentUser.getRole().getName(); // Admin / Manager / Staff


        // 1. Validate email
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException(AccountErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. Validate phone
        if (request.getPhone() != null &&
                accountRepository.existsByPhone(request.getPhone())) {
            throw new AppException(AccountErrorCode.PHONE_ALREADY_EXISTS);
        }

        // 3. Validate role input
        String roleNameInput = request.getRoleName();
        if (roleNameInput == null || roleNameInput.isBlank()) {
            throw new AppException(AccountErrorCode.INVALID_ROLE);
        }

        // Chuẩn hóa role
        String normalizedRole = normalizeRoleName(roleNameInput);

        // Chỉ cho phép 3 role hợp lệ
        if (!List.of("Admin", "Manager", "Staff").contains(normalizedRole)) {
            throw new AppException(AccountErrorCode.INVALID_ROLE);
        }


        // ===== 4. RBAC - CHẶN PHÂN QUYỀN =====

        // Staff không được tạo account
        if ("Staff".equals(currentRole)) {
            throw new AppException(AccountErrorCode.FORBIDDEN_ACTION);
        }

        // Manager không được tạo Manager hoặc Admin
        if ("Manager".equals(currentRole)) {
            if ("Manager".equals(normalizedRole) || "Admin".equals(normalizedRole)) {
                throw new AppException(AccountErrorCode.FORBIDDEN_ACTION);
            }
        }

        // 5. Lấy role từ DB
        Role role = roleRepository.findByName(normalizedRole)
                .orElseThrow(() -> new AppException(AccountErrorCode.INVALID_ROLE));


        // 6. Validate password
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new AppException(AccountErrorCode.INVALID_PASSWORD);
        }

        // 7. Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());


        // 8. Create account
        Account account = Account.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(encodedPassword)
                .role(role)
                .branchId(request.getBranchId())
                .build();

        accountRepository.save(account);


        // 9. Response
        return CreateAccountResponse.builder()
                .accountId(account.getAccountId())
                .fullName(account.getFullName())
                .email(account.getEmail())
                .role(role.getName())
                .branchId(account.getBranchId())
                .build();
    }
}