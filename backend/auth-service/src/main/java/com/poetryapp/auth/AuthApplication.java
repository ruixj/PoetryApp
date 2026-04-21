package com.poetryapp.auth;

import com.poetryapp.auth.entity.User;
import com.poetryapp.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log4j2
@SpringBootApplication
@EnableDiscoveryClient
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    /**
     * 初始化管理员账号（仅当密码为 PENDING_INIT 时执行）
     */
    @Bean
    ApplicationRunner adminInitRunner(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepo.findByPhone("13800000000").ifPresent(admin -> {
                if ("PENDING_INIT".equals(admin.getPassword())) {
                    admin.setPassword(passwordEncoder.encode("Admin@123"));
                    userRepo.save(admin);
                    log.info("管理员账号初始化完成，初始密码: Admin@123，请登录后立即修改！");
                }
            });
        };
    }
}
