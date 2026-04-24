package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum TicketErrorCode implements BaseErrorCode {
    TICKET_NOT_FOUND(OK, "TIC-001", "Vé không tồn tại", new HashMap<>()),
    TICKET_STATUS_NOT_FOUND(OK, "TIC-002", "Trạng thái không tồn tại", new HashMap<>()),
    SEAT_ALREADY_BOOKED(OK, "TIC-003", "Ghế đã được đặt, vui lòng chọn ghế khác", new HashMap<>()),
    ;
    private final HttpStatusCode statusCode;
    private final String code; // Đảm bảo `code` là String
    private final String message;
    private final Map<String, Optional<?>> result;

    TicketErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
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

