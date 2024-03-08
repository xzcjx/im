package com.xzccc.server;

import com.xzccc.model.Dao.User;
import com.xzccc.model.Vo.*;
import com.xzccc.model.request.ProcessFriendRequest;
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

  void update_status(Long userId, String sessionId, Short status);

  List<FriendStatusResponse> get_friend_status(Long userId);

  void read_friend(Long userId);

  void update_username(Long userId, String username);

  SearchUserResponse search_account(String account);

  List<SessionResponse> get_sessions(Long userId);
}
