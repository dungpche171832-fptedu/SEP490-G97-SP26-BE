package vn.edu.fpt.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.entity.BaseEntity;
import vn.edu.fpt.enums.AccountRole;
import vn.edu.fpt.enums.AccountStatus;

@Entity
@Table(
        name = "account",
        indexes = {
                @Index(name = "idx_account_role", columnList = "role"),
                @Index(name = "idx_account_branch", columnList = "branch_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AccountRole role;

    @Column(name = "branch_id")
    private Long branchId;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;
}
