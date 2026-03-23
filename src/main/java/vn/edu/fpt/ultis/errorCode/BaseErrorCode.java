package vn.edu.fpt.ultis.errorCode;

import org.springframework.http.HttpStatusCode;

import java.util.Map;
import java.util.Optional;

public interface BaseErrorCode {
    String getCode();
    String getMessage();
    HttpStatusCode getStatusCode();
    Map<String, Optional<?>> getResult();
}