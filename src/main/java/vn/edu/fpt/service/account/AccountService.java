package vn.edu.fpt.service.account;

import vn.edu.fpt.entity.Account;
import vn.edu.fpt.ultis.enums.AccountRole;

import java.util.List;

public interface AccountService {
    List<Account> getAccountsByRoleAndFilter(List<AccountRole> roles, Long branchId, String email);
}