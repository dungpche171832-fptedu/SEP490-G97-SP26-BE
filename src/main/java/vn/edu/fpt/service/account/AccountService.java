package vn.edu.fpt.service.account;

import vn.edu.fpt.entity.Account;

import java.util.List;

public interface AccountService {
    List<Account> getAccountsByRoleAndFilter(List<String> roles, Long branchId, String email);
}