// package com.xzccc.server.wx.impl;
//
// import com.xzccc.server.utils.SignatureUtil;
// import com.xzccc.server.wx.IWeiXinValidateService;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
//
// @Service
// public class WeiXinValidateService implements IWeiXinValidateService {
//    @Value("${wx.config.token}")
//    private String token;
//
//
//    @Override
//    public boolean checkSign(String signature, String timestamp, String nonce) {
//        return SignatureUtil.check(token, signature, timestamp, nonce);
//    }
// }
