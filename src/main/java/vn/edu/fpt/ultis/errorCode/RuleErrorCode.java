package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum RuleErrorCode implements BaseErrorCode {
    RULE_LIST_EMPTY(OK, "RUL-001", "Danh sách rule không được để trống", new HashMap<>()),
    INVALID_MIN_KM(OK, "RUL-002", "Khoảng cách bắt đầu không hợp lệ", new HashMap<>()),
    INVALID_MAX_KM(OK, "RUL-003", "Khoảng cách kết thúc không hợp lệ", new HashMap<>()),
    INVALID_PRICE(OK, "RUL-004", "Giá tiền không hợp lệ", new HashMap<>()),
    RULE_MUST_START_FROM_ZERO(OK, "RUL-005", "Rule đầu tiên phải bắt đầu từ 0 km", new HashMap<>()),
    RULE_RANGE_NOT_CONTINUOUS(OK, "RUL-006", "Các khoảng cách phải nối tiếp nhau và không được chồng lấn", new HashMap<>()),
    RULE_LAST_MAX_MUST_BE_NULL(OK, "RUL-007", "Rule cuối cùng phải có maxKm là null", new HashMap<>()),
    RULE_NOT_FOUND(OK, "RUL-008", "Không tìm thấy rule", new HashMap<>());

    private final HttpStatusCode statusCode;
    private final String code;
    private final String message;
    private final Map<String, Optional<?>> result;

    RuleErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
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