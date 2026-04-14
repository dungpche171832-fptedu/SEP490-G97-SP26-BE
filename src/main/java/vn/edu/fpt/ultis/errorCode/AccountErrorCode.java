package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum AccountErrorCode implements BaseErrorCode {
    ACCOUNT_NOT_ACTIVE(OK, "ACC-001", "Tài khoản đã bị khóa", new HashMap<>()),
    ACCOUNT_INVALID(OK, "ACC-002", "Sai tài khoản hoặc mật khẩu", new HashMap<>()),
    ACCOUNT_NOT_FOUND(OK, "ACC-003", "Tài khoản không tồn tại", new HashMap<>()),
    EMAIL_ALREADY_EXISTS(OK, "ACC-004", "Email đã tồn tại", new HashMap<>()),
    PHONE_ALREADY_EXISTS(OK, "ACC-005", "Số điện thoại đã tồn tại", new HashMap<>()),
    INVALID_CURRENT_PASSWORD(OK, "ACC-006", "Mật khẩu hiện tại không đúng", new HashMap<>()),
    NEW_PASSWORD_CONFIRM_NOT_MATCH(OK, "ACC-007", "Xác nhận mật khẩu mới không khớp", new HashMap<>()),
    INVALID_PASSWORD(OK, "ACC-008", "Mật khẩu không hợp lệ", new HashMap<>()),
    INVALID_ROLE(OK, "ACC-009", "Vai trò không hợp lệ", new HashMap<>()),
    FORBIDDEN_ACTION(OK, "ACC-010", "Bạn không có quyền thao tác chức năng này", new HashMap<>()),
    INVALID_NEW_PASSWORD(OK, "ACC-011", "Mật khẩu mới không hợp lệ", new HashMap<>()),
    ROLE_NOT_FOUND(OK, "ACC-012", "Role không hợp lệ", new HashMap<>()),
    ;
    private final HttpStatusCode statusCode;
    private final String code; // Đảm bảo `code` là String
    private final String message;
    private final Map<String, Optional<?>> result;

    AccountErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
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

