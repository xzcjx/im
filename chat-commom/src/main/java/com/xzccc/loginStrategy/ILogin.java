package com.xzccc.loginStrategy;

import com.xzccc.model.Dao.User;

public interface ILogin {
    void check(String account, String password);

    User verify(String account, String password);

    String create_token(User user);

    User get_user(String account);
}
