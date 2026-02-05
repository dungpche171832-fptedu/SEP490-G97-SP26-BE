package vn.edu.fpt.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AccountController {

    @GetMapping
    public String getAccount() {
        return "List accounts";
    }
}
