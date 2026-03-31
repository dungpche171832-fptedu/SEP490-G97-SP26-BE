package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.entity.PasswordResetOtp;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID> {

    Optional<PasswordResetOtp> findTopByEmailOrderByCreatedAtDesc(String email);

    long countByEmailAndCreatedAtAfter(String email, LocalDateTime time);

    @Modifying
    @Query("UPDATE PasswordResetOtp o SET o.isUsed = true WHERE o.email = :email AND o.isUsed = false")
    void invalidateAllByEmail(String email);

    @Modifying
    @Query(value = """
    DELETE FROM password_reset_otp
    WHERE expired_at < NOW() - INTERVAL 1 DAY
""", nativeQuery = true)
    int deleteExpiredOtp();
}