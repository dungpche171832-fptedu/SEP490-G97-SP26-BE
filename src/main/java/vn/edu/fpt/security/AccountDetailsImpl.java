package vn.edu.fpt.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.edu.fpt.entity.Account;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Boolean isActive;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Build AccountDetails từ entity Account
     */
    public static AccountDetailsImpl build(Account account) {

        // Vì role là 1 ENUM trong bảng account
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + account.getRole().name())
        );

        return new AccountDetailsImpl(
                account.getAccountId(),
                account.getUsername(),
                account.getEmail(),
                account.getPassword(),
                account.getIsActive(),
                authorities
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Quyết định user có login được hay không
     */
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive);
    }
}
