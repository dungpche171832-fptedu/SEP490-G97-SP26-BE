package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.entity.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<Account> findByRole_NameIn(List<String> roleNames);

    List<Account> findByRole_NameInAndBranchId(List<String> roleNames, Long branchId);

    List<Account> findByRole_NameInAndEmailContainingIgnoreCase(List<String> roleNames, String email);

    List<Account> findByRole_NameInAndBranchIdAndEmailContainingIgnoreCase(List<String> roleNames, Long branchId, String email);
}