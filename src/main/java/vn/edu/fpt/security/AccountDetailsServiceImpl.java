package vn.edu.fpt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.repository.AccountRepository;
import vn.edu.fpt.dto.response.account.AccountResponse;

@Service
@RequiredArgsConstructor
public class AccountDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Account not found: " + email)
                );

        if (Boolean.FALSE.equals(account.getIsActive())) {
            throw new DisabledException("Account is inactive");
        }

        return AccountDetailsImpl.build(account);
    }

    // Phương thức để lấy thông tin tài khoản cá nhân
    @Transactional(readOnly = true)
    public AccountResponse getAccountInfoByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Account not found: " + email)
                );

        // Trả về DTO chứa thông tin tài khoản
        return new AccountResponse(
                account.getFullName(),
                account.getEmail(),
                account.getPhone(),
                account.getRole().getRoleId(),
                account.getBranchId()
        );
    }
}