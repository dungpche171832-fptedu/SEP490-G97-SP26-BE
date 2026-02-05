package vn.edu.fpt.controller;

import vn.edu.fpt.dto.request.LoginRequest;
import vn.edu.fpt.dto.request.LogoutRequest;
import vn.edu.fpt.dto.request.RefreshTokenRequest;
import vn.edu.fpt.dto.request.RegisterRequest;
import vn.edu.fpt.dto.response.LoginResponse;
import vn.edu.fpt.dto.response.MessageResponse;
import vn.edu.fpt.dto.response.RefreshTokenResponse;
import vn.edu.fpt.service.AuthService;
import vn.edu.fpt.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

    /**
     * POST /api/auth/login
     * Đăng nhập – tất cả role dùng chung API
     */
    @PostMapping("/login")
    @Operation(
            summary = "Đăng nhập",
            description = "Xác thực username/password và trả về JWT + role để frontend điều hướng"
    )
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register
     * (Nếu hệ thống nhà xe cho phép CUSTOMER tự đăng ký)
     */
    @PostMapping("/register")
    @Operation(summary = "Đăng ký",
            description = "Tạo tài khoản CUSTOMER mới"
    )
    public ResponseEntity<MessageResponse> register(
            @RequestBody RegisterRequest request
    ) {
        authService.register(request);
        return ResponseEntity.ok(
                new MessageResponse("Đăng ký thành công")
        );
    }

    /**
     * POST /api/auth/refresh-token
     * Làm mới access token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
                authService.refreshToken(request.getRefreshToken())
        );
    }


    /**
     * POST /api/auth/logout
     * Đăng xuất
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {

        refreshTokenService.logoutByRefreshToken(request.getRefreshToken());

        return ResponseEntity.ok("Logout thành công");
    }
}
