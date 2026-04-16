package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum PlanErrorCode implements BaseErrorCode {

    PLAN_CAR_NOT_FOUND(OK, "PLAN-001", "Xe không tồn tại", new HashMap<>()),
    PLAN_DRIVER_NOT_FOUND(OK, "PLAN-002", "Tài xế không tồn tại", new HashMap<>()),
    PLAN_STATION_NOT_FOUND(OK, "PLAN-003", "Station không tồn tại", new HashMap<>()),
    PLAN_DUPLICATE_STATION(OK, "PLAN-004", "Station bị trùng trong plan", new HashMap<>()),
    PLAN_DUPLICATE_ORDER(OK, "PLAN-005", "stationOrder bị trùng", new HashMap<>()),
    PLAN_ALREADY_EXISTS(OK, "PLAN-006", "Xe đã có lịch tại thời điểm này", new HashMap<>()),
    PLAN_INVALID_STATION_LIST(OK, "PLAN-007", "Danh sách station không hợp lệ", new HashMap<>()),
    PLAN_SEAT_TEMPLATE_NOT_ENOUGH(OK, "PLAN-008", "Không đủ ghế mẫu để khởi tạo cho chuyến", new HashMap<>()),
    INVALID_TOTAL_SEAT(OK, "PLAN-009", "Số lượng ghế của xe không hợp lệ", new HashMap<>()),
    INVALID_TIME_RANGE(OK, "PLAN-010", "Thời lượng không hợp lệ", new HashMap<>()),
    PLAN_CODE_ALREADY_EXISTS(OK, "PLAN-011", "Mã lịch trình đã tồn tại", new HashMap<>()),
    PLAN_NOT_FOUND(OK, "PLAN-012", "lịch trình không tồn tại", new HashMap<>()),
    ;

    private final HttpStatusCode statusCode;
    private final String code;
    private final String message;
    private final Map<String, Optional<?>> result;

    PlanErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
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