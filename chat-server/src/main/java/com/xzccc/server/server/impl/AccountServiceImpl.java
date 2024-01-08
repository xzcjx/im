package com.xzccc.server.server.impl;

import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.exception.BusinessException;
import com.xzccc.server.mapper.FriendShipMapper;
import com.xzccc.server.mapper.UserMapper;
import com.xzccc.server.model.Dao.FriendShip;
import com.xzccc.server.model.Dao.User;
import com.xzccc.server.server.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    FriendShipMapper friendShipMapper;

    @Override
    public User get_user(long id) {
        return userMapper.select_by_id(id);
    }

    @Override
    public void add_friend(Long uid, Long friendId) {
        FriendShip friendShip = friendShipMapper.select_by_uid_friend_id(uid, friendId);
        if(friendShip!=null){
            throw new BusinessException(ErrorCode.FRIENDEXISTS);
        }
        friendShipMapper.insert(uid, friendId);
    }

    @Override
    public void note_friend(Long uid, Long friendId, String note) {
        friendShipMapper.update_note(uid,friendId,note);
    }
}
