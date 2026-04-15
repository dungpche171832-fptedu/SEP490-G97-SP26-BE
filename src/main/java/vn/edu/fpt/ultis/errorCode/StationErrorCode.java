package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum StationErrorCode implements BaseErrorCode {
    STATION_NOT_FOUND(OK, "STA-001", "Điểm dừng không tồn tại", new HashMap<>()),
    CITY_NOT_FOUND(OK, "STA-002", "Thành phố không tồn tại", new HashMap<>()),
    STATION_NAME_EXISTS(OK, "STA-003", "Tên điểm dừng đã tồn tại", new HashMap<>()),
    STATION_CODE_EXISTS(OK, "STA-004", "Mã điểm dừng đã tồn tại", new HashMap<>()),
    INVALID_LATITUDE(OK, "STA-005", "Vĩ độ không hợp lệ (-90 đến 90)", new HashMap<>()),
    INVALID_LONGITUDE(OK, "STA-006", "Kinh độ không hợp lệ (-180 đến 180)", new HashMap<>()),
    STATION_NAME_REQUIRED(OK, "STA-007", "Tên điểm dừng không được để trống", new HashMap<>()),
    STATION_CODE_REQUIRED(OK, "STA-008", "Mã điểm dừng không được để trống", new HashMap<>()),
    CITY_ID_REQUIRED(OK, "STA-009", "CityId không được để trống", new HashMap<>()),
   ;
    private final HttpStatusCode statusCode;
    private final String code; // Đảm bảo `code` là String
    private final String message;
    private final Map<String, Optional<?>> result;

    StationErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
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

