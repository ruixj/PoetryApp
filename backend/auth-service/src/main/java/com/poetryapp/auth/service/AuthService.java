package com.poetryapp.auth.service;

import com.poetryapp.auth.dto.*;
import com.poetryapp.auth.entity.LoginRecord;
import com.poetryapp.auth.entity.SmsCode;
import com.poetryapp.auth.entity.User;
import com.poetryapp.auth.repository.LoginRecordRepository;
import com.poetryapp.auth.repository.SmsCodeRepository;
import com.poetryapp.auth.repository.UserRepository;
import com.poetryapp.common.constant.ResponseCode;
import com.poetryapp.common.exception.BusinessException;
import com.poetryapp.common.utils.JwtUtils;
import com.poetryapp.common.utils.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final LoginRecordRepository loginRecordRepo;
    private final SmsCodeRepository smsCodeRepo;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // ── 发送验证码 ─────────────────────────────────────────────
    public void sendSmsCode(String phone) {
        // 清除旧验证码
        smsCodeRepo.deleteByPhone(phone);

        String code = generateCode();
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        smsCodeRepo.insert(smsCode);

        smsService.send(phone, code);
        log.info("短信验证码已发送, phone={}", phone);
    }

    // ── 注册 ───────────────────────────────────────────────────
    @Transactional
    public LoginResponse register(RegisterRequest req) {
        if (userRepo.existsByPhone(req.getPhone())) {
            throw new BusinessException(ResponseCode.PHONE_EXISTS, "该手机号已注册");
        }

        verifySmsCode(req.getPhone(), req.getSmsCode());

        User user = new User();
        user.setPhone(req.getPhone());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname() != null && !req.getNickname().isBlank()
                ? req.getNickname() : NicknameGenerator.generate());
        userRepo.insert(user);

        return buildLoginResponse(user);
    }

    // ── 密码登录 ───────────────────────────────────────────────
    @Transactional
    public LoginResponse loginByPassword(LoginByPasswordRequest req) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new BusinessException(ResponseCode.AUTH_FAIL, "账号或密码错误"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.AUTH_FAIL, "账号或密码错误");
        }

        recordLogin(user.getId());
        return buildLoginResponse(user);
    }

    // ── 短信验证码登录 ─────────────────────────────────────────
    @Transactional
    public LoginResponse loginBySms(LoginBySmsRequest req) {
        verifySmsCode(req.getPhone(), req.getSmsCode());

        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND, "账号不存在，请先注册"));

        recordLogin(user.getId());
        return buildLoginResponse(user);
    }

    // ── 退出登录 ───────────────────────────────────────────────
    @Transactional
    public void logout(Long userId) {
        loginRecordRepo
                .findTopByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(userId)
                .ifPresent(record -> {
                    LocalDateTime now = LocalDateTime.now();
                    record.setLogoutTime(now);
                    long minutes = ChronoUnit.MINUTES.between(record.getLoginTime(), now);
                    record.setDurationMinutes((int) minutes);
                    loginRecordRepo.update(record);

                    // 累计学习时长
                    userRepo.findById(userId).ifPresent(u -> {
                        u.setTotalStudyMinutes(u.getTotalStudyMinutes() + (int) minutes);
                        u.setIsFirstLogin(false);
                        userRepo.update(u);
                    });
                    log.info("用户退出, userId={}, 本次时长={}min", userId, minutes);
                });
    }

    // ── 私有方法 ───────────────────────────────────────────────
    private void verifySmsCode(String phone, String code) {
        SmsCode smsCode = smsCodeRepo
                .findTopByPhoneAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(phone, LocalDateTime.now())
                .orElseThrow(() -> new BusinessException(ResponseCode.SMS_CODE_ERROR, "验证码不存在或已过期"));

        if (!smsCode.getCode().equals(code)) {
            throw new BusinessException(ResponseCode.SMS_CODE_ERROR, "验证码错误");
        }

        smsCode.setIsUsed(true);
        smsCodeRepo.update(smsCode);
    }

    private void recordLogin(Long userId) {
        LoginRecord record = new LoginRecord();
        record.setUserId(userId);
        record.setLoginTime(LocalDateTime.now());
        loginRecordRepo.insert(record);
    }

    private LoginResponse buildLoginResponse(User user) {
        String token = jwtUtils.generateToken(user.getId(), user.getPhone(), user.getRole());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isFirstLogin(user.getIsFirstLogin())
                .build();
    }

    private String generateCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
