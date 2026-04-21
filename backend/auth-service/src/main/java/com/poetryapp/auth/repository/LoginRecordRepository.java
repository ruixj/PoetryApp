package com.poetryapp.auth.repository;

import com.poetryapp.auth.entity.LoginRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoginRecordRepository extends JpaRepository<LoginRecord, Long> {
    /** 查找用户最近一条未退出记录（logout_time 为 null） */
    Optional<LoginRecord> findTopByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(Long userId);
}
