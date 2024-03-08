package com.xzccc.loginStrategy;

import com.xzccc.common.ErrorCode;
import com.xzccc.exception.BusinessException;
import com.xzccc.model.Dao.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AccountLogin extends AbstractLoginTemplate {
  @Override
  public void check(String account, String password) {
    if (StringUtils.isBlank(account) || account.length() < 3 || account.length() > 20) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    if (StringUtils.isBlank(password)) {
      throw new BusinessException(ErrorCode.PASSWORD_ERROR);
    }
  }

  @Override
  public User get_user(String account) {
    return userMapper.select_by_account(account);
  }
}
