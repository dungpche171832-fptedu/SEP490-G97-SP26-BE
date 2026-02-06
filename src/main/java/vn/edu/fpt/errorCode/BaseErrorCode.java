package vn.edu.fpt.errorCode;

import org.springframework.http.HttpStatusCode;

import java.util.Map;
import java.util.Optional;

public interface BaseErrorCode {
    HttpStatusCode getStatusCode(); // Mã trạng thái HTTP tương ứng

    Map<String, Optional<?>> getResult(); // Trả về kết quả dưới dạng Object (hoặc Optional)

    String getCode(); // Mã lỗi

    String getMessage(); // Thông điệp lỗi
}
