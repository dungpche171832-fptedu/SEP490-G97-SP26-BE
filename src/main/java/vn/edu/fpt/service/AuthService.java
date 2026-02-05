package vn.edu.fpt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.LoginRequest;
import vn.edu.fpt.dto.request.RegisterRequest;
import vn.edu.fpt.dto.response.LoginResponse;
import vn.edu.fpt.dto.response.RefreshTokenResponse;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.entity.RefreshToken;
import vn.edu.fpt.enums.AccountRole;
import vn.edu.fpt.enums.AccountStatus;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final AuthenticationManager authenticationManager;
        private final AccountRepository accountRepository;
        private final JwtUtil jwtUtil;
        private final RefreshTokenService refreshTokenService;
        private final PasswordEncoder passwordEncoder;

        // ================= LOGIN =================
        @Transactional
        public LoginResponse login(LoginRequest request) {

                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                Account account = accountRepository.findByUsername(request.getUsername())
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                String accessToken = jwtUtil.generateAccessToken(account);
                RefreshToken refreshToken =
                        refreshTokenService.createRefreshToken(account.getAccountId());

                LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                        .id(account.getAccountId())
                        .username(account.getUsername())
                        .fullName(account.getFullName())
                        .role(account.getRole().name())
                        .branchId(account.getBranchId())
                        .build();

                return LoginResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .user(userInfo)
                        .build();
        }

        // ================= REGISTER =================
        @Transactional
        public void register(RegisterRequest request) {

                if (accountRepository.existsByUsername(request.getUsername())) {
                        throw new RuntimeException("Username đã tồn tại");
                }

                if (accountRepository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException("Email đã tồn tại");
                }

                String encodedPassword =
                        passwordEncoder.encode(request.getPassword());

                Account account = Account.builder()
                        .username(request.getUsername())
                        .password(encodedPassword)   // ✅ BẮT BUỘC
                        .fullName(request.getFullName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .role(AccountRole.CUSTOMER)
                        .status(AccountStatus.ACTIVE)
                        .build();

                accountRepository.save(account);
        }

        // ================= REFRESH TOKEN =================
        @Transactional
        public RefreshTokenResponse refreshToken(String refreshToken) {

                RefreshToken tokenEntity =
                        refreshTokenService.verifyExpiration(refreshToken);

                Account account = tokenEntity.getAccount();

                String newAccessToken = jwtUtil.generateAccessToken(account);

                return RefreshTokenResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build();
        }

}
