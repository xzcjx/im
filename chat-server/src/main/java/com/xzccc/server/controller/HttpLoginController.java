package com.xzccc.server.controller;


import com.xzccc.common.BaseResponse;
import com.xzccc.model.request.HttpLoginRequest;
import com.xzccc.model.request.HttpSignRequest;
import com.xzccc.server.HttpLoginUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RequestMapping("/auth")
@RestController
@Slf4j
public class HttpLoginController {

    @Autowired
    HttpLoginUserService httpLoginUserService;

    @PostMapping("/login")
    public BaseResponse login(HttpLoginRequest body) {
        return httpLoginUserService.login(body);
    }

    @PostMapping("/sign")
    public BaseResponse sign(HttpSignRequest body) {
        return httpLoginUserService.sign(body);
    }

    @GetMapping("/email/code")
    public BaseResponse email_code(String email){
        httpLoginUserService.email_code(email);
        return new BaseResponse(true);
    }
}
