package com.xzccc.server;


import com.xzccc.common.BaseResponse;
import com.xzccc.model.request.HttpLoginRequest;
import com.xzccc.model.request.HttpSignRequest;

public interface HttpLoginUserService {

    BaseResponse login(HttpLoginRequest body);

    BaseResponse sign(HttpSignRequest body);

    void email_code(String email);
}
