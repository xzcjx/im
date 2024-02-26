package com.xzccc.server.controller;

import com.xzccc.common.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/${app.config.api-version}/wx")
@RestController
@Api(tags = "微信端相关登录")
public class WxController {

    @ApiOperation("获取公众号二维码")
    @GetMapping("getGzhQrcode")
    public BaseResponse getGzhQrcode() {
//        String uuid = EncryptUtils.UUIDLowerCase(true);
//        return new WebResult<>(mpaTUserWechatService.getGzhQrcode(uuid));
        return null;
    }
}
