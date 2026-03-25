package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum BranchErrorCode implements BaseErrorCode {
    BRANCH_CODE_ALREADY_EXISTS(OK, "BR-001", "Mã chi nhánh đã tồn tại", new HashMap<>()),
    MANAGER_ACCOUNT_NOT_FOUND(OK, "BR-002", "Tài khoản quản lý không tồn tại", new HashMap<>()),
    BRANCH_LIST_EMPTY(OK, "BR-003", "Không có chi nhánh nào", new HashMap<>()),
    ;
    private final HttpStatusCode statusCode;
    private final String code; // Đảm bảo `code` là String
    private final String message;
    private final Map<String, Optional<?>> result;

    BranchErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
        this.result = result;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public Map<String, Optional<?>> getResult() {
        return result;
    }
}