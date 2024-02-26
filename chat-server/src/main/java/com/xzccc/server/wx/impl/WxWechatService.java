package com.xzccc.server.wx.impl;

import com.xzccc.common.ErrorCode;
import com.xzccc.constant.RedisConstant;
import com.xzccc.exception.BusinessException;
import com.xzccc.server.utils.SignatureUtil;
import com.xzccc.server.utils.WxMessageUtil;
import com.xzccc.server.wx.IWxWechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Map;

@Service
@Slf4j
public class WxWechatService implements IWxWechatService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Override
    public String receive(String signature, String timestamp, String nonce, String echostr, HttpServletRequest request) {
        log.info("微信回调方法开始执行");
        String token = "xxxxx自己随便设置，但是要和公众号的token一样";
        // 验证微信签名
        if (!SignatureUtil.check(token, signature, timestamp, nonce)) {
            throw new BusinessException(ErrorCode.WxCallError);
        }
        // 验证服务端配置
        if (echostr != null) {
            return echostr;
        }
        // 接收微信推送的消息
        String xmlString = null;
        try {
            xmlString = WxMessageUtil.readRequest(request);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        try {
            Map<String, String> resXml = WxMessageUtil.ResponseXmlToMap(xmlString);
            String ticket = resXml.get("Ticket"); // 获取二维码凭证
            String gzhOpenid = resXml.get("FromUserName"); // 获取OpenId
            String eventType = resXml.get("Event"); // 获取事件类型
            String msgType = resXml.get("MsgType");
            stringRedisTemplate.delete(RedisConstant.WxToken+":"+ticket);
//            stringRedisTemplate.opsForValue().set(RedisEnum.GZH_EWM_PREFIX + ticket, gzhOpenid, Duration.ofSeconds(280));
            if ("subscribe".equals(eventType)) { // 如果是订阅消息
                String subscribeContent = "感谢关注";
                stringRedisTemplate.opsForValue().set(RedisConstant.WxToken+":"+ticket, gzhOpenid, Duration.ofSeconds(280));
                return WxMessageUtil.getWxReturnMsg(resXml, subscribeContent);
            }
            if ("SCAN".equals(eventType)) { // 如果是扫码消息
                String scanContent = "扫码成功";
//                stringRedisTemplate.opsForValue().set(RedisEnum.GZH_SAO_MA + ticket, "1", Duration.ofSeconds(280));
                return WxMessageUtil.getWxReturnMsg(resXml, scanContent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("微信回调方法执行结束！");
        return "";
    }
}
