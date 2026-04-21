package com.poetryapp.auth.repository;

import com.poetryapp.auth.entity.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {

    /** 查找最新有效验证码 */
    Optional<SmsCode> findTopByPhoneAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String phone, LocalDateTime now);

    /** 清除手机号所有旧验证码 */
    @Modifying
    @Query("DELETE FROM SmsCode s WHERE s.phone = :phone")
    void deleteByPhone(String phone);
}
