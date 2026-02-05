package vn.edu.fpt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.entity.RefreshToken;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;

    private static final long REFRESH_TOKEN_DURATION_DAYS = 7;

    // ================= CREATE =================
    @Transactional
    public RefreshToken createRefreshToken(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Xoá token cũ nếu có
        refreshTokenRepository.deleteByAccountAccountId(accountId);

        RefreshToken refreshToken = RefreshToken.builder()
                .account(account)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(REFRESH_TOKEN_DURATION_DAYS))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // ================= VERIFY =================
    @Transactional(readOnly = true)
    public RefreshToken verifyExpiration(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token đã hết hạn");
        }

        return refreshToken;
    }

    // ================= DELETE =================
    @Transactional
    public void deleteByAccountId(Long accountId) {
        refreshTokenRepository.deleteByAccountAccountId(accountId);
    }
    @Transactional
    public void logout(Long accountId) {
        refreshTokenRepository.deleteByAccountAccountId(accountId);
    }
    @Transactional
    public void logoutByRefreshToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại"));

        refreshTokenRepository.delete(token);
    }

}
