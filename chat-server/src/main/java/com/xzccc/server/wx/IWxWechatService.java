package com.xzccc.server.wx;

import javax.servlet.http.HttpServletRequest;

public interface IWxWechatService {
  String receive(
      String signature, String timestamp, String nonce, String echostr, HttpServletRequest request);
}
