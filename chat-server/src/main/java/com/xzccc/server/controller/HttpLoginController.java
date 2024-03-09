package com.xzccc.server.controller;

import com.xzccc.common.BaseResponse;
import com.xzccc.model.request.HttpLoginRequest;
import com.xzccc.model.request.HttpSignRequest;
import com.xzccc.server.HttpLoginUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/${app.config.api-version}/auth")
@RestController
@Slf4j
public class HttpLoginController {

    @Autowired
    HttpLoginUserService httpLoginUserService;

    @PostMapping("/login")
    public BaseResponse login(@RequestBody HttpLoginRequest body) {
        return httpLoginUserService.login(body);
    }

    @PostMapping("/register")
    public BaseResponse register(@RequestBody HttpSignRequest body) {
        return httpLoginUserService.register(body);
    }

    @GetMapping("/email/code")
    public BaseResponse email_code(String email) {
        httpLoginUserService.email_code(email);
        return new BaseResponse(true);
    }
}
