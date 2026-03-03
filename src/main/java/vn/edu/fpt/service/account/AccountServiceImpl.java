package vn.edu.fpt.service.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.ultis.enums.AccountRole;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Account> getAccountsByRoleAndFilter(List<AccountRole> roles, Long branchId, String email) {
        if (branchId == null && (email == null || email.isEmpty())) {
            // Nếu không có branchId và email, chỉ lọc theo roles
            return accountRepository.findByRoleIn(roles);
        } else if (branchId != null && (email == null || email.isEmpty())) {
            // Nếu có branchId nhưng email trống, lọc theo branchId và roles
            return accountRepository.findByRoleInAndBranchId(roles, branchId);
        } else if (branchId != null) {
            // Nếu có cả branchId và email, lọc theo cả hai
            return accountRepository.findByRoleInAndBranchIdAndEmailContainingIgnoreCase(roles, branchId, email);
        } else {
            // Nếu chỉ có email và không có branchId, lọc theo email
            return accountRepository.findByRoleInAndEmailContainingIgnoreCase(roles, email);
        }
    }
}