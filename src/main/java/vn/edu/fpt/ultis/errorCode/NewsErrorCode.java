package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

public enum NewsErrorCode implements BaseErrorCode {

    NEWS_NOT_FOUND(OK, "NEWS-001", "News không tồn tại", new HashMap<>()),

    TITLE_REQUIRED(OK, "NEWS-002", "Tiêu đề không được để trống", new HashMap<>()),

    INVALID_TIME_RANGE(OK, "NEWS-003", "Thời gian kết thúc phải sau thời gian bắt đầu", new HashMap<>()),

    IMAGE_REQUIRED(OK, "NEWS-004", "Ảnh không được để trống", new HashMap<>()),

    INVALID_DISPLAY_ORDER(OK, "NEWS-005", "Thứ tự hiển thị không hợp lệ", new HashMap<>()),

    NEWS_NOT_AVAILABLE(OK, "NEWS-002", "Tin tức hiện không khả dụng", new HashMap<>()
    );
    ;

    private final HttpStatusCode statusCode;
    private final String code;
    private final String message;
    private final Map<String, Optional<?>> result;

    NewsErrorCode(HttpStatusCode statusCode, String code, String message, Map<String, Optional<?>> result) {
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
