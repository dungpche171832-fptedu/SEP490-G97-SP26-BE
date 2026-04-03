package vn.edu.fpt.service.account;

import vn.edu.fpt.dto.request.account.UpdateProfileRequest;
import vn.edu.fpt.dto.request.account.ChangePasswordRequest;
import vn.edu.fpt.dto.response.account.AccountResponse;
import vn.edu.fpt.entity.Account;

import java.util.List;

public interface AccountService {
    List<Account> getAccountsByRoleAndFilter(List<String> roles, Long branchId, String email);

    AccountResponse updateProfile(UpdateProfileRequest request);
    void changePassword(ChangePasswordRequest request);
}