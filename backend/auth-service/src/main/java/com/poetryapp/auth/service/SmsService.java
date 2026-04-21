package com.poetryapp.auth.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 阿里云短信服务
 *
 * 需在 application.yml 配置：
 *   aliyun.sms.access-key-id
 *   aliyun.sms.access-key-secret
 *   aliyun.sms.sign-name
 *   aliyun.sms.template-code
 *
 * 生产环境请在阿里云控制台申请签名和模板。
 */
@Log4j2
@Service
public class SmsService {

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name}")
    private String signName;

    @Value("${aliyun.sms.template-code}")
    private String templateCode;

    @Value("${aliyun.sms.enabled:false}")
    private boolean enabled;

    /**
     * 发送验证码短信
     * enabled=false 时仅打印日志（本地开发用）
     */
    public void send(String phone, String code) {
        if (!enabled) {
            log.info("[SMS DEV] phone={} code={}", phone, code);
            return;
        }
        try {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint("dysmsapi.aliyuncs.com");

            Client client = new Client(config);

            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.sendSms(request);
            if (!"OK".equals(response.getBody().getCode())) {
                log.error("短信发送失败: phone={}, result={}", phone, response.getBody().getMessage());
                throw new RuntimeException("短信发送失败，请稍后重试");
            }
            log.info("短信发送成功: phone={}", phone);
        } catch (Exception e) {
            log.error("短信服务异常: phone={}", phone, e);
            throw new RuntimeException("短信服务异常，请稍后重试");
        }
    }
}
