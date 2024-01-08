package com.xzccc.server.server;

import com.xzccc.server.common.BaseResponse;
import com.xzccc.server.model.request.HttpLoginRequest;
import com.xzccc.server.model.request.HttpSignRequest;

public interface HttpLoginUserService {

    BaseResponse login(HttpLoginRequest body);

    BaseResponse sign(HttpSignRequest body);
}
