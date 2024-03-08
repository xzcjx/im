package com.xzccc.server.wx;

public interface IWeiXinValidateService {
  boolean checkSign(String signature, String timestamp, String nonce);
}
