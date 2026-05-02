package vn.edu.fpt.dto.response.account;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.ultis.enums.AccountStatus;

@Getter
@Setter
@Builder
public class UpdateAccountStatusResponse {

    private Long accountId;

    private String fullName;

    private String email;

    private AccountStatus oldStatus;

    private AccountStatus newStatus;
}