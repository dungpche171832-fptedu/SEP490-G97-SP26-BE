package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.response.AccountResponse;
import vn.edu.fpt.security.AccountDetailsServiceImpl;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountDetailsServiceImpl accountDetailsService;
    @GetMapping("/info")
    public AccountResponse getAccountInfo(@RequestParam String email) {
        return accountDetailsService.getAccountInfoByEmail(email);
    }
}