package com.poetryapp.auth.repository;

import com.poetryapp.auth.entity.SmsCode;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Mapper
public interface SmsCodeRepository {

    @Select("SELECT * FROM sms_codes WHERE phone=#{phone} AND is_used=0 AND expires_at > #{now} "
          + "ORDER BY created_at DESC LIMIT 1")
    Optional<SmsCode> findTopByPhoneAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String phone, LocalDateTime now);

    @Delete("DELETE FROM sms_codes WHERE phone = #{phone}")
    void deleteByPhone(String phone);

    @Insert("INSERT INTO sms_codes(phone, code, expires_at, is_used, created_at) "
          + "VALUES(#{phone}, #{code}, #{expiresAt}, #{isUsed}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SmsCode smsCode);

    @Update("UPDATE sms_codes SET is_used=#{isUsed} WHERE id=#{id}")
    void update(SmsCode smsCode);
}
