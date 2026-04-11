package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum CarErrorCode implements BaseErrorCode {

    CAR_NOT_FOUND(OK, "CAR-001", "Xe không tồn tại", new HashMap<>()),
    LICENSE_PLATE_ALREADY_EXISTS(OK, "CAR-002", "Biển số xe đã tồn tại", new HashMap<>()),
    BRANCH_NOT_FOUND(OK, "CAR-003", "Chi nhánh không tồn tại", new HashMap<>()),
    INVALID_TOTAL_SEAT(OK, "CAR-004", "Số ghế không hợp lệ", new HashMap<>()),
    INVALID_MANUFACTURE_YEAR(OK, "CAR-005", "Năm sản xuất không hợp lệ", new HashMap<>()),
    CAR_NOT_ACTIVE(OK, "CAR-006", "Xe không hoạt động", new HashMap<>()),
    ;

    private final HttpStatusCode statusCode;
    private final String code;
    private final String message;
    private final Map<String, Optional<?>> result;

    CarErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
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