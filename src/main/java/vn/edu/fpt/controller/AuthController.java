package vn.edu.fpt.controller;

import vn.edu.fpt.dto.request.auth.*;
import vn.edu.fpt.dto.response.LoginResponse;
import vn.edu.fpt.dto.response.MessageResponse;
import vn.edu.fpt.dto.response.RefreshTokenResponse;
import vn.edu.fpt.service.AuthService;
import vn.edu.fpt.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.service.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs xác thực hệ thống nhà xe")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    @Operation(
            summary = "Đăng nhập",
            description = "Xác thực username/password và trả về JWT + role để frontend điều hướng"
    )
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký", description = "Tạo tài khoản CUSTOMER mới")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new MessageResponse("Đăng ký thành công"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody LogoutRequest request) {
        refreshTokenService.logoutByRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new MessageResponse("Logout thành công"));
    }
<<<<<<< HEAD
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password updated");
    }
=======
>>>>>>> 67646dda1be898d9206eb73bdebe02959ae8fff4
}