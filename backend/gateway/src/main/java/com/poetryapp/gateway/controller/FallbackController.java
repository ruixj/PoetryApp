package com.poetryapp.gateway.controller;

import com.poetryapp.common.constant.ResponseCode;
import com.poetryapp.common.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public ApiResponse<Void> fallback() {
        return ApiResponse.fail(ResponseCode.SERVICE_FALLBACK, "服务暂时不可用，请稍后重试");
    }
}
