package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.account.ChangePasswordRequest;
import vn.edu.fpt.dto.request.account.UpdateProfileRequest;
import vn.edu.fpt.dto.response.account.AccountListResponse;
import vn.edu.fpt.dto.response.account.AccountResponse;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.security.AccountDetailsServiceImpl;
import vn.edu.fpt.service.account.AccountService;

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
        List<String> roles = Arrays.asList("Staff", "Manager", "Admin");

        List<Account> accounts = accountService.getAccountsByRoleAndFilter(roles, branchId, email);

        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new AccountListResponse(accounts, "Không có tài khoản nào", 0));
        }

        return ResponseEntity.ok(
                new AccountListResponse(accounts, "Danh sách tài khoản", accounts.size())
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<AccountResponse> updateProfile(
            @RequestBody UpdateProfileRequest request) {

        return ResponseEntity.ok(accountService.updateProfile(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        accountService.changePassword(request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
}