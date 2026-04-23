package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum RouteErrorCode implements BaseErrorCode {

    ROUTE_CODE_EXISTS(OK, "ROU-001", "Mã route đã tồn tại", new HashMap<>()),
    ROUTE_NAME_REQUIRED(OK, "ROU-002", "Tên route không được để trống", new HashMap<>()),
    ROUTE_CODE_REQUIRED(OK, "ROU-003", "Code route không được để trống", new HashMap<>()),
    STATION_LIST_EMPTY(OK, "ROU-004", "Danh sách station không được rỗng", new HashMap<>()),
    STATION_NOT_FOUND(OK, "ROU-005", "Station không tồn tại", new HashMap<>()),
    ROUTE_NOT_FOUND(OK, "ROU-006", "Station không tồn tại", new HashMap<>()),
    ;

    private final HttpStatusCode statusCode;
    private final String code;
    private final String message;
    private final Map<String, Optional<?>> result;

    RouteErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
        this.result = result;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }

    @Override
    public HttpStatusCode getStatusCode() { return statusCode; }

    @Override
    public Map<String, Optional<?>> getResult() { return result; }
}