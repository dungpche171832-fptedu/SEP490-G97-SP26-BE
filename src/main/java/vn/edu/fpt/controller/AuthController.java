package vn.edu.fpt.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/hello")
    public String hello() {
        return "Auth API is running";
    }
}
