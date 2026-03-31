package vn.edu.fpt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.auth.LoginRequest;
import vn.edu.fpt.dto.request.auth.RegisterRequest;
import vn.edu.fpt.dto.request.auth.ResetPasswordRequest;
import vn.edu.fpt.dto.response.LoginResponse;
import vn.edu.fpt.dto.response.RefreshTokenResponse;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.entity.PasswordResetOtp;
import vn.edu.fpt.entity.RefreshToken;
import vn.edu.fpt.entity.Role;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.PasswordResetOtpRepository;
import vn.edu.fpt.repository.RoleRepository;
import vn.edu.fpt.service.email.EmailService;
import vn.edu.fpt.ultis.enums.AccountStatus;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.security.JwtUtil;
import vn.edu.fpt.ultis.errorCode.AccountErrorCode;
import vn.edu.fpt.ultis.errorCode.AuthErrorCode;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final AuthenticationManager authenticationManager;
        private final AccountRepository accountRepository;
        private final JwtUtil jwtUtil;
        private final RefreshTokenService refreshTokenService;
        private final PasswordEncoder passwordEncoder;
        private final RoleRepository roleRepository;
        private final PasswordResetOtpRepository otpRepository;
        private final EmailService emailService;

        @Transactional
        public LoginResponse login(LoginRequest request) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        request.getEmail(),
                                        request.getPassword()
                                )
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (org.springframework.security.core.AuthenticationException e) {
                        throw new AppException(AccountErrorCode.ACCOUNT_INVALID);
                }

                Account account = accountRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));

                if (!Boolean.TRUE.equals(account.getIsActive())) {
                        throw new AppException(AccountErrorCode.ACCOUNT_NOT_ACTIVE);
                }

                String accessToken = jwtUtil.generateAccessToken(account);
                RefreshToken refreshToken =
                        refreshTokenService.createRefreshToken(account.getAccountId());

                LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                        .id(account.getAccountId())
                        .fullName(account.getFullName())
                        .role(account.getRole().getName())
                        .branchId(account.getBranchId())
                        .build();

                return LoginResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .user(userInfo)
                        .build();
        }
        @Transactional
        public void register(RegisterRequest request) {

                if (accountRepository.existsByEmail(request.getEmail())) {
                        throw new AppException(AccountErrorCode.EMAIL_ALREADY_EXISTS);
                }

                if (accountRepository.existsByPhone(request.getPhone())) {
                        throw new AppException(AccountErrorCode.PHONE_ALREADY_EXISTS);
                }

                String encodedPassword = passwordEncoder.encode(request.getPassword());

                Role customerRole = roleRepository.findByName("Customer")
                        .orElseThrow(() -> new RuntimeException("Role Customer not found"));

                Account account = Account.builder()
                        .password(encodedPassword)
                        .fullName(request.getFullName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .role(customerRole)
                        .status(AccountStatus.ACTIVE)
                        .build();

                accountRepository.save(account);
        }

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
        @Transactional
        public void forgotPassword(String email) {

                // check account tồn tại
                accountRepository.findByEmail(email)
                        .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));

                // rate limit: max 5 lần / 10 phút
                long count = otpRepository.countByEmailAndCreatedAtAfter(
                        email,
                        java.time.LocalDateTime.now().minusMinutes(10)
                );

                if (count >= 5) {
                        throw new AppException(AuthErrorCode.TOO_MANY_REQUEST);
                }

                // check OTP chưa hết hạn
                otpRepository.findTopByEmailOrderByCreatedAtDesc(email)
                        .ifPresent(old -> {
                                if (!old.getIsUsed()
                                        && old.getExpiredAt().isAfter(java.time.LocalDateTime.now())) {
                                        throw new AppException(AuthErrorCode.OTP_NOT_EXPIRED);
                                }
                        });

                // generate OTP
                String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

                // invalidate OTP cũ
                otpRepository.invalidateAllByEmail(email);

                // save OTP mới
                PasswordResetOtp entity = PasswordResetOtp.builder()
                        .email(email)
                        .otp(otp)
                        .expiredAt(java.time.LocalDateTime.now().plusMinutes(5))
                        .isUsed(false)
                        .build();

                otpRepository.save(entity);

                // gửi email
                emailService.sendOtp(email, otp);
        }
        @Transactional
        public void resetPassword(ResetPasswordRequest request) {

                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                        throw new AppException(AuthErrorCode.PASSWORD_NOT_MATCH);
                }

                PasswordResetOtp otpEntity = otpRepository
                        .findTopByEmailOrderByCreatedAtDesc(request.getEmail())
                        .orElseThrow(() -> new AppException(AuthErrorCode.OTP_NOT_FOUND));

                if (otpEntity.getIsUsed()) {
                        throw new AppException(AuthErrorCode.OTP_ALREADY_USED);
                }

                if (!otpEntity.getOtp().equals(request.getOtp())) {
                        throw new AppException(AuthErrorCode.OTP_INVALID);
                }

                if (otpEntity.getExpiredAt().isBefore(java.time.LocalDateTime.now())) {
                        throw new AppException(AuthErrorCode.OTP_EXPIRED);
                }

                Account account = accountRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));

                account.setPassword(passwordEncoder.encode(request.getNewPassword()));

                otpEntity.setIsUsed(true);

                accountRepository.save(account);
                otpRepository.save(otpEntity);
        }
}