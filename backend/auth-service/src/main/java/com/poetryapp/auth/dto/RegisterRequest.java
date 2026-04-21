package com.poetryapp.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    private String password;

    /** 可选昵称，为空时系统自动生成 */
    private String nickname;

    @NotBlank(message = "短信验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码为6位")
    private String smsCode;
}
