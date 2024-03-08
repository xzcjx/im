// package com.xzccc.server.controller;
//
// import com.xzccc.server.wx.IWxWechatService;
// import com.xzccc.server.wx.impl.WeiXinValidateService;
// import com.xzccc.server.wx.impl.WxWechatService;
// import io.swagger.annotations.ApiOperation;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.*;
//
// import javax.servlet.http.HttpServletRequest;
// import java.io.IOException;
//
// @Slf4j
// @RestController
// @RequestMapping("/wx/portal/{appid}")// /api/${app.config.api-version}/
// public class WxConfigController {
//
//    @Autowired
//    WeiXinValidateService   weiXinValidateService;
//    @Autowired
//    WxWechatService wxWechatService;
//    @GetMapping() //produces = "text/plain;charset=utf-8"
//    public String validate(@PathVariable String appid,HttpServletRequest request) {
//        try {
//            String signature = request.getParameter ("signature");
//            String timestamp = request.getParameter ("timestamp");
//            String nonce = request.getParameter ("nonce");
//            String echostr = request.getParameter ("echostr");
//            log.info("微信公众号验签信息{}开始 [{}, {}, {}, {}]", appid, signature, timestamp, nonce,
// echostr);
//            if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
//                throw new IllegalArgumentException("请求参数非法，请核实!");
//            }
//            boolean check = weiXinValidateService.checkSign(signature, timestamp, nonce);
//            log.info("微信公众号验签信息{}完成 check：{}", appid, check);
//            if (!check) {
//                return null;
//            }
//            return echostr;
//        } catch (Exception e) {
//            log.error("微信公众号验签信息{}失败 [{}]", appid, e);
//            return null;
//        }
//    }
//
//    @GetMapping(value = "/receive")
//    @ApiOperation("接收微信消息事件,判断用户是否完成扫码关注")
//    public String getGzhLoginReceive(HttpServletRequest request) throws IOException {
//        log.info("微信回调接口开始执行get请求/weixin/receive");
//        // 获取微信请求参数
//        String signature = request.getParameter("signature");
//        String timestamp = request.getParameter("timestamp");
//        String nonce = request.getParameter("nonce");
//        String echostr = request.getParameter("echostr");
//        log.info("开始校验此次消息是否来自微信服务器，param->signature:{},\ntimestamp:{},\nnonce:{},\nechostr:{}",
//                signature, timestamp, nonce, echostr);
//        String result = wxWechatService.receive(signature, timestamp, nonce, echostr, request);
//        System.out.println(result);
//        log.info("微信回调接口get请求执行结束！");
//        return result;
//    }
//
//    @PostMapping(value = "/receive")
//    @ApiOperation("接收微信消息事件,判断用户是否完成扫码关注")
//    public String postGzhLoginReceive(HttpServletRequest request) throws IOException {
//        log.info("微信回调接口开始执行post请求/weixin/receive");
//        // 获取微信请求参数
//        String signature = request.getParameter("signature");
//        String timestamp = request.getParameter("timestamp");
//        String nonce = request.getParameter("nonce");
//        String echostr = request.getParameter("echostr");
//        log.info("开始校验此次消息是否来自微信服务器，param->signature:{},\ntimestamp:{},\nnonce:{},\nechostr:{}",
//                signature, timestamp, nonce, echostr);
//        String result = wxWechatService.receive(signature, timestamp, nonce, echostr, request);
//        System.out.println(result);
//        log.info("微信回调接口post请求执行结束！");
//        return result;
//    }
// }
