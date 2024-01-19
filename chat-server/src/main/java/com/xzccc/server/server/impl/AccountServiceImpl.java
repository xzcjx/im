package com.xzccc.server.server.impl;

import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.constant.RedisConstant;
import com.xzccc.server.exception.BusinessException;
import com.xzccc.server.mapper.FriendShipMapper;
import com.xzccc.server.mapper.UserMapper;
import com.xzccc.server.model.Dao.FriendShip;
import com.xzccc.server.model.Dao.User;
import com.xzccc.server.model.Redis.UserToken;
import com.xzccc.server.server.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    FriendShipMapper friendShipMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ValueOperations<String,Object> valueOperations;

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

    @Override
    public void kick_user(String user_id) {
        log.info(user_id+" 该账号已经登录，开始踢人下线");
        String key = RedisConstant.UserToken + ":" + user_id;
        UserToken userToken= (UserToken) valueOperations.get(key);
        String token = userToken.getToken();
        redisTemplate.delete(key);
        key=RedisConstant.TokenUser + ":" + token;
        redisTemplate.delete(key);
    }

}
