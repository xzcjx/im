package com.xzccc.server.controller;


import com.xzccc.server.common.BaseResponse;
import com.xzccc.server.model.request.HttpLoginRequest;
import com.xzccc.server.model.request.HttpSignRequest;
import com.xzccc.server.server.HttpLoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;

@RequestMapping("/auth")
@RestController
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
}
