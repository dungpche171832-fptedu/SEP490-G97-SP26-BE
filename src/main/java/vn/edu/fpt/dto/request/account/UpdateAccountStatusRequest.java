package vn.edu.fpt.dto.request.account;

import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.ultis.enums.AccountStatus;

@Getter
@Setter
public class UpdateAccountStatusRequest {

    private AccountStatus status;
}