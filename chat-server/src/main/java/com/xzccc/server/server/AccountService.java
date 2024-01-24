package com.xzccc.server.server;

import com.xzccc.server.model.Dao.User;
import com.xzccc.server.model.Vo.FriendResponse;
import com.xzccc.server.model.Vo.FriendShipRequestsResponse;
import com.xzccc.server.model.request.ProcessFriendRequest;

import java.util.List;

public interface AccountService {

    User get_user(long id);

    void add_friend(Long uid, Long friendId, String ps);

    void note_friend(Long uid, Long friendId, String note);

    void kick_user(String user_id);

    void delete_friend(Long userId, Long friendId);

    void process_friend(Long userId, ProcessFriendRequest processFriendRequest);

    List<FriendShipRequestsResponse> get_friend_requests(Long userId, Long page, Long pagesize);

    List<FriendResponse> get_friends(Long userId);

    String create_session(Long userId, Long friendId);

    void update_status(Long userId, Long friendId, String sessionId, Short status);
}
