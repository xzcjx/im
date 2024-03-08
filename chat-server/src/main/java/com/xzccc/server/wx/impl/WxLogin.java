// package com.xzccc.server.wx.impl;
//
// import com.xzccc.constant.RedisConstant;
// import com.xzccc.server.jsonUtil.JSONStrategy;
// import com.xzccc.server.utils.OkHttpClientUtil;
// import com.xzccc.server.wx.IWxLogin;
// import com.xzccc.server.wx.model.AccessTokenResponse;
// import com.xzccc.server.wx.model.TicketResponse;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.data.redis.core.ValueOperations;
// import org.springframework.scheduling.annotation.EnableScheduling;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;
//
// @Service
//
// public class WxLogin implements IWxLogin {
//    @Value("${wx.config.appid}")
//    String appid;
//    @Value("${wx.config.appSecret}")
//    String appSecret;
//    @Value("${wx.config.token}")
//    String token;
//    @Value("${wx.config.accessTokenUrl}")
//    String accessTokenUrl;
//    @Autowired
//    JSONStrategy jsonStrategy;
//    @Autowired
//    ValueOperations<String,Object> valueOperations;
//
//    @Scheduled(fixedRate = 7100000)
//    @Override
//    public void get_access_token() {
//        String url = accessTokenUrl.replace("APPID", appid).replace("APPSECRET", appSecret);
//        String s = OkHttpClientUtil.doGet(url);
//        AccessTokenResponse accessTokenResponse = jsonStrategy.fastjsonFromJson(s,
// AccessTokenResponse.class);
//        valueOperations.set(RedisConstant.AccessToken,accessTokenResponse);
//    }
//
//    @Override
//    public TicketResponse get_ticket(String access_token) {
//        return null;
//    }
// }
