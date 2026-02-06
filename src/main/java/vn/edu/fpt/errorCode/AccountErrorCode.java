package vn.edu.fpt.errorCode;

import org.springframework.http.HttpStatusCode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum AccountErrorCode implements BaseErrorCode {
    ACCOUNT_NOT_ACTIVE(OK, "ACC-001", "Tài khoản đã bị khóa", new HashMap<>()),
    ACCOUNT_INVALID(OK, "ACC-002", "Sai tài khoản hoặc mật khẩu", new HashMap<>()),
    ACCOUNT_NOT_FOUND(OK, "ACC-003", "Tài khoản không tồn tại", new HashMap<>()),
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

