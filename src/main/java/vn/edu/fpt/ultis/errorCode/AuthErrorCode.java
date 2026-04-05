package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum AuthErrorCode implements BaseErrorCode {

    OTP_NOT_FOUND(OK, "AUTH-001", "Không tìm thấy OTP", new HashMap<>()),
    OTP_INVALID(OK, "AUTH-002", "OTP không hợp lệ", new HashMap<>()),
    OTP_EXPIRED(OK, "AUTH-003", "OTP đã hết hạn", new HashMap<>()),
    OTP_ALREADY_USED(OK, "AUTH-004", "OTP đã được sử dụng", new HashMap<>()),
    PASSWORD_NOT_MATCH(OK, "AUTH-005", "Mật khẩu không khớp", new HashMap<>()),
    TOO_MANY_REQUEST(OK, "AUTH-006", "Bạn đã yêu cầu quá nhiều lần, vui lòng thử lại sau", new HashMap<>()),
    OTP_NOT_EXPIRED(OK, "AUTH-007", "OTP hiện tại vẫn còn hiệu lực", new HashMap<>()),
    ;

    private final HttpStatusCode statusCode;
    private final String code;
    private final String message;
    private final Map<String, Optional<?>> result;

    AuthErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public HttpStatusCode getStatusCode() { return statusCode; }
    public Map<String, Optional<?>> getResult() { return result; }
}