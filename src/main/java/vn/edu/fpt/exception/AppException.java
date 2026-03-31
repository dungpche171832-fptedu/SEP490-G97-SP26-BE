package vn.edu.fpt.exception;

import lombok.Getter;
import vn.edu.fpt.ultis.errorCode.BaseErrorCode;

@Getter
public class AppException extends RuntimeException {
    private final BaseErrorCode errorCode;

    public AppException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}