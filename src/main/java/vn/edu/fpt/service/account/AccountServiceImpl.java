package vn.edu.fpt.service.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.repository.AccountRepository;


import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

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
}