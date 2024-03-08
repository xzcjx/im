package com.xzccc.loginStrategy;

import com.xzccc.common.ErrorCode;
import com.xzccc.constant.LoginConstant;
import com.xzccc.exception.BusinessException;
import com.xzccc.model.request.HttpLoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginStrategy {
  @Autowired EmailLogin emailLogin;
  @Autowired AccountLogin accountLogin;

  public String login(HttpLoginRequest httpLoginRequest, String type) {
    if (LoginConstant.EMAIL_TYPE.equals(type)) {
      return emailLogin.login(httpLoginRequest);
    } else if (LoginConstant.ACCOUNT_TYPE.equals(type)) {
      return accountLogin.login(httpLoginRequest);
    } else throw new BusinessException(ErrorCode.PARAMS_ERROR);
  }
}
