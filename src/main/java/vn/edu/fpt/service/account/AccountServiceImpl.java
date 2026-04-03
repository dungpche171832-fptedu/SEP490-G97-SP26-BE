package vn.edu.fpt.service.account;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.dto.request.account.UpdateProfileRequest;
import vn.edu.fpt.dto.request.account.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.dto.response.account.AccountResponse;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.ultis.errorCode.AccountErrorCode;


import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Account> getAccountsByRoleAndFilter(List<String> roles, Long branchId, String email) {
        List<String> normalizedRoles = roles.stream()
                .map(this::normalizeRoleName)
                .toList();

        if (branchId == null && (email == null || email.isEmpty())) {
            return accountRepository.findByRole_NameIn(normalizedRoles);
        } else if (branchId != null && (email == null || email.isEmpty())) {
            return accountRepository.findByRole_NameInAndBranchId(normalizedRoles, branchId);
        } else if (branchId != null) {
            return accountRepository.findByRole_NameInAndBranchIdAndEmailContainingIgnoreCase(normalizedRoles, branchId, email);
        } else {
            return accountRepository.findByRole_NameInAndEmailContainingIgnoreCase(normalizedRoles, email);
        }
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
            throw new AppException(AccountErrorCode.INVALID_NEW_PASSWORD);
        }
        // 5. Check mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
            throw new AppException(AccountErrorCode.INVALID_CURRENT_PASSWORD);
        }

        // 6. Encode mật khẩu mới
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // 7. Save
        accountRepository.save(account);
    }
    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Z]).{8,}$");
    }
}