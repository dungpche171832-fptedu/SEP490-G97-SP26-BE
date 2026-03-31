package vn.edu.fpt.dto.response.account;

import vn.edu.fpt.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AccountListResponse {
    private List<Account> accounts;  // Danh sách các tài khoản
    private String message;  // Thông báo trả về
    private int totalCount;  // Tổng số tài khoản
}