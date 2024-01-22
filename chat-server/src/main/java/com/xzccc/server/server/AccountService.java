package com.xzccc.server.server;

import com.xzccc.server.model.Dao.User;

public interface AccountService {

    User get_user(long id);

    void add_friend(Long uid, Long friendId,String ps);

    void note_friend(Long uid, Long friendId, String note);

    void kick_user(String user_id);

    void delete_friend(Long userId, Long friendId);
}
