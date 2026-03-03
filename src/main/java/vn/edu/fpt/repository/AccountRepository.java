package vn.edu.fpt.repository;

import vn.edu.fpt.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.ultis.enums.AccountRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByPhone(String phoneNumber);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phoneNumber);

    // Tìm kiếm theo roles và branchId
    List<Account> findByRoleInAndBranchId(List<AccountRole> roles, Long branchId);

    // Tìm kiếm theo roles và email
    List<Account> findByRoleInAndEmailContainingIgnoreCase(List<AccountRole> roles, String email);

    // Tìm kiếm theo roles, branchId và email
    List<Account> findByRoleInAndBranchIdAndEmailContainingIgnoreCase(List<AccountRole> roles, Long branchId, String email);

    // Tìm kiếm theo chỉ role
    List<Account> findByRoleIn(List<AccountRole> roles);

}
