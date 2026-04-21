package com.poetryapp.common.constant;

/**
 * 统一响应码定义
 */
public final class ResponseCode {

    private ResponseCode() {}

    // ── 成功 ────────────────────────────────────────
    public static final int SUCCESS = 200;

    // ── 客户端错误 4xx ───────────────────────────────
    /** 请求参数错误 */
    public static final int BAD_REQUEST       = 400;
    /** 未登录 / Token 无效 */
    public static final int UNAUTHORIZED      = 401;
    /** 无权限 */
    public static final int FORBIDDEN         = 403;
    /** 资源不存在 */
    public static final int NOT_FOUND         = 404;
    /** 业务逻辑错误 */
    public static final int BUSINESS_ERROR    = 422;
    /** 验证码错误 */
    public static final int SMS_CODE_ERROR    = 4001;
    /** 手机号已注册 */
    public static final int PHONE_EXISTS      = 4002;
    /** 账号或密码错误 */
    public static final int AUTH_FAIL         = 4003;
    /** Token 已过期，需要重新登录 */
    public static final int TOKEN_EXPIRED     = 4004;
    /** 积分不足 */
    public static final int POINTS_NOT_ENOUGH = 4005;
    /** 库存不足 */
    public static final int STOCK_NOT_ENOUGH  = 4006;

    // ── 服务端错误 5xx ───────────────────────────────
    public static final int INTERNAL_ERROR    = 500;
    /** 服务不可用（熔断） */
    public static final int SERVICE_FALLBACK  = 503;
}
