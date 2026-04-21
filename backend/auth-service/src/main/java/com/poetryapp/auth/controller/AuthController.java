package com.poetryapp.auth.controller;

import com.poetryapp.auth.dto.*;
import com.poetryapp.auth.service.AuthService;
import com.poetryapp.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 发送短信验证码 */
    @PostMapping("/sms/send")
    public ApiResponse<Void> sendSms(
            @RequestParam @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        authService.sendSmsCode(phone);
        return ApiResponse.success("验证码已发送", null);
    }

    /** 注册 */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResponse.success(authService.register(req));
    }

    /** 密码登录 */
    @PostMapping("/login/password")
    public ApiResponse<LoginResponse> loginByPassword(@Valid @RequestBody LoginByPasswordRequest req) {
        return ApiResponse.success(authService.loginByPassword(req));
    }

    /** 短信验证码登录 */
    @PostMapping("/login/sms")
    public ApiResponse<LoginResponse> loginBySms(@Valid @RequestBody LoginBySmsRequest req) {
        return ApiResponse.success(authService.loginBySms(req));
    }

    /** 退出登录（需传 X-User-Id 由 Gateway 注入） */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("X-User-Id") Long userId) {
        authService.logout(userId);
        return ApiResponse.success("已退出登录", null);
    }
}
