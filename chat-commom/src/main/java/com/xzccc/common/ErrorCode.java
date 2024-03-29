package com.xzccc.common;

public enum ErrorCode {
    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    SENSITIVE_ERROR(40000, "包含铭感词", ""),
    EMAIL_EXISTS(40000, "邮箱已注册", ""),
    ACCOUNT_SET_ERROR(40000, "账号不允许设置为邮箱", ""),
    ACCOUNT_EXISTS(40000, "账号重复", ""),
    LOGIN_TYPE_ERROR(40000, "登录模式错误", ""),
    USER_ERROR(40000, "账号不存在", ""),
    PASSWORD_ERROR(40000, "密码错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "无权限", ""),
    EXP_AUTH(40101, "token过期", ""),
    FORBIDDEN(40301, "禁止操作", ""),
    SYSTEM_ERROR(50000, "系统内部异常", ""),
    FRIEND_EXISTS(40001, "已经是好友，请勿重复添加", ""),
    FRIEND_NOT_EXISTS(40301, "好友关系不存在", ""),
    FRIEND_LIMIT(40001, "超过好友限制个数", ""),
    SESSION_EXISTS(40001, "会话已经存在", ""),
    WX_CALL_ERROR(50001, "微信回调参数异常！", ""),
    EMAIL_LIMIT_ERROR(40000, "邮箱发送次数过多，请稍后重试！", ""),
    EMAIL_CODE_ERROR(40000, "验证码错误", "");

    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 状态码描述（详情）
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
