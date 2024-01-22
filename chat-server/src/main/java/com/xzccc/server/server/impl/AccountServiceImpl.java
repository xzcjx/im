package com.xzccc.server.server.impl;

import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.constant.ImRelationshipSponsor;
import com.xzccc.server.constant.ImRelationshipStatus;
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

import java.util.Date;

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
    public void add_friend(Long userId, Long friendId,String ps) {
        FriendShip friendShip = friendShipMapper.select_by_user_id_friend_id(userId, friendId);
        if(friendShip!=null){
            if (friendShip.getStatus()!= ImRelationshipStatus.AGREE) {
                friendShipMapper.update_status(userId,friendId,ImRelationshipStatus.AGREE);
                return;
            }
            throw new BusinessException(ErrorCode.FRIENDEXISTS);
        }
        friendShipMapper.insert(ImRelationshipSponsor.SPONSOR,userId, friendId, ImRelationshipStatus.AGREE,ps);
        friendShipMapper.insert(ImRelationshipSponsor.NOTSPONSOR,friendId, userId, ImRelationshipStatus.UNREAD,ps);
        // 此处需要通过websocket通知对方，如果对方在线，需要使用mq，后面在家逻辑
    }

    @Override
    public void note_friend(Long userId, Long friendId, String note) {
        friendShipMapper.update_note(userId,friendId,note);
    }

    @Override
    public void kick_user(String userId) {
        log.info(userId+" 该账号已经登录，开始踢人下线");
        String key = RedisConstant.UserToken + ":" + userId;
        UserToken userToken= (UserToken) valueOperations.get(key);
        String token = userToken.getToken();
        redisTemplate.delete(key);
        key=RedisConstant.TokenUser + ":" + token;
        redisTemplate.delete(key);
    }

    @Override
    public void delete_friend(Long userId, Long friendId) {
        FriendShip friendShip = friendShipMapper.select_by_user_id_friend_id(userId, friendId);
        if (friendShip==null) {
            throw new BusinessException(ErrorCode.FRIENDNOTEXISTS);
        }
        friendShipMapper.delete(userId,friendId,new Date());
    }

}
