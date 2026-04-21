package com.poetryapp.common.exception;

import com.poetryapp.common.constant.ResponseCode;
import lombok.Getter;

/**
 * 业务异常，携带 code 和提示信息
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ResponseCode.BUSINESS_ERROR;
    }
}
