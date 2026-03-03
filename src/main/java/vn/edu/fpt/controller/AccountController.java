package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.response.account.AccountListResponse;
import vn.edu.fpt.dto.response.account.AccountResponse;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.security.AccountDetailsServiceImpl;
import vn.edu.fpt.service.account.AccountService;
import vn.edu.fpt.ultis.enums.AccountRole;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountDetailsServiceImpl accountDetailsService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/info")
    public AccountResponse getAccountInfo(@RequestParam String email) {
        return accountDetailsService.getAccountInfoByEmail(email);
    }

    @GetMapping
    public ResponseEntity<AccountListResponse> getAccounts(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String email
    ) {
        // Danh sách role cần lọc
        List<AccountRole> roles = Arrays.asList(AccountRole.STAFF, AccountRole.BRANCH_MANAGER, AccountRole.ADMIN);

        // Lấy danh sách tài khoản có role STAFF, BRANCH_MANAGER, ADMIN và lọc theo branchId, email
        List<Account> accounts;

        // Kiểm tra nếu không có branchId và email, lấy tất cả tài khoản có role STAFF, BRANCH_MANAGER, ADMIN
        if (email == null && branchId == null) {
            accounts = accountService.getAccountsByRoleAndFilter(roles, null, null);
        } else {
            accounts = accountService.getAccountsByRoleAndFilter(roles, branchId, email);
        }

        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new AccountListResponse(accounts, "Không có tài khoản nào", 0));
        }

        return ResponseEntity.ok(new AccountListResponse(accounts, "Danh sách tài khoản", accounts.size()));
    }
}