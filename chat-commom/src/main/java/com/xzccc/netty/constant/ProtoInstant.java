package com.xzccc.netty.constant;

public class ProtoInstant {

  /** 魔数，可以通过配置获取 */
  public static final short MAGIC_CODE = 0x86;

  /** 版本号 */
  public static final short VERSION_CODE = 0x01;

  /** 返回码枚举类 */
  public enum ResultCodeEnum {
    SUCCESS(0, "Success"), // 成功
    AUTH_FAILED(1, "登录失败"),
    NO_TOKEN(2, "没有授权码"),
    UNKNOW_ERROR(3, "未知错误"),
    ;

    private Integer code;
    private String desc;

    ResultCodeEnum(Integer code, String desc) {
      this.code = code;
      this.desc = desc;
    }

    public Integer getCode() {
      return code;
    }

    public String getDesc() {
      return desc;
    }
  }

  /** 客户端平台 */
  public interface Platform {
    /** WEB端 */
    public static final int WEB = 1;

    /** 未知 */
    public static final int UNKNOWN = 2;
  }
}
