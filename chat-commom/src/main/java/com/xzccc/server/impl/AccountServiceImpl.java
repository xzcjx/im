package com.xzccc.server.impl;


import com.xzccc.common.ErrorCode;
import com.xzccc.constant.ImRelationshipStatus;
import com.xzccc.constant.ImSessionStatus;
import com.xzccc.constant.RedisConstant;
import com.xzccc.exception.BusinessException;
import com.xzccc.mapper.FriendShipMapper;
import com.xzccc.mapper.SessionMapper;
import com.xzccc.mapper.UserMapper;
import com.xzccc.model.Dao.FriendShip;
import com.xzccc.model.Dao.Session;
import com.xzccc.model.Dao.User;
import com.xzccc.model.Redis.UserToken;
import com.xzccc.model.Vo.FriendResponse;
import com.xzccc.model.Vo.FriendShipRequestsResponse;
import com.xzccc.model.request.ProcessFriendRequest;
import com.xzccc.server.AccountService;
import com.xzccc.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


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
    ValueOperations<String, Object> valueOperations;

    @Autowired
    UUIDUtils uuidUtils;

    @Autowired
    SessionMapper sessionMapper;

    @Value("${friendship.friend_limit}")
    Long friendLimit;

    @Override
    public User get_user(long id) {
        return userMapper.select_by_id(id);
    }

    @Override
    public void add_friend(Long userId, Long friendId, String ps) {
        Long count = friendShipMapper.count_by_userId(userId);
        if (count >= friendLimit) {
            throw new BusinessException(ErrorCode.FRIENDLIMIT);
        }
        FriendShip friendShip = friendShipMapper.select_by_userId_friendId(userId, friendId);
        if (friendShip != null) {
            if (friendShip.getStatus() < ImRelationshipStatus.AGREE) {
                friendShipMapper.update_status(userId, friendId, ImRelationshipStatus.AGREE);
                return;
            }
            throw new BusinessException(ErrorCode.FRIENDEXISTS);
        }
        friendShipMapper.insert(userId, friendId, ImRelationshipStatus.SPONSOR, ps);
        friendShipMapper.insert(friendId, userId, ImRelationshipStatus.UNREAD, ps);
        // 此处需要通过websocket通知对方，如果对方在线，需要使用mq，后面在家逻辑
    }

    @Override
    public void note_friend(Long userId, Long friendId, String note) {
        friendShipMapper.update_note(userId, friendId, note);
    }

    @Override
    public void kick_user(String userId) {
        log.info(userId + " 该账号已经登录，开始踢人下线");
        String key = RedisConstant.UserToken + ":" + userId;
        UserToken userToken = (UserToken) valueOperations.get(key);
        String token = userToken.getToken();
        redisTemplate.delete(key);
        key = RedisConstant.TokenUser + ":" + token;
        redisTemplate.delete(key);
    }

    @Override
    public void delete_friend(Long userId, Long friendId) {
        FriendShip friendShip = friendShipMapper.select_by_userId_friendId(userId, friendId);
        if (friendShip == null) {
            throw new BusinessException(ErrorCode.FRIENDNOTEXISTS);
        }
        friendShipMapper.delete(userId, friendId, new Date());
    }

    @Override
    public void process_friend(Long userId, ProcessFriendRequest processFriendRequest) {
        Long friendId = processFriendRequest.getFriendId();
        Boolean agree = processFriendRequest.getAgree();
        if (agree == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (agree) {
            Long count = friendShipMapper.count_by_userId(userId);
            if (count >= friendLimit) {
                throw new BusinessException(ErrorCode.FRIENDLIMIT);
            }
            friendShipMapper.update_status(userId, friendId, ImRelationshipStatus.AGREE);
        } else {
            friendShipMapper.update_status(userId, friendId, ImRelationshipStatus.REJECT);
        }
    }

    @Override
    public List<FriendShipRequestsResponse> get_friend_requests(Long userId, Long page, Long pagesize) {
        List<FriendShipRequestsResponse> friendShipList=friendShipMapper.page_by_userId(userId,(page-1)*pagesize,pagesize);
        return friendShipList;
    }

    @Override
    public List<FriendResponse> get_friends(Long userId) {
        List<FriendResponse> friendResponses=friendShipMapper.select_friend_info(userId);
        return friendResponses;
    }

    @Override
    public String create_session(Long userId, Long friendId) {
        FriendShip friendShip = friendShipMapper.select_by_userId_friendId(userId, friendId);
        if (friendShip == null) {
            throw new BusinessException(ErrorCode.FRIENDNOTEXISTS);
        }
        Session session = sessionMapper.select_session_by_user(userId, friendId);
        if (session != null) {
            if(session.getStatus()!= ImSessionStatus.DISPLAY){
                sessionMapper.update_status(session.getId(),ImSessionStatus.DISPLAY);
            }
            return session.getSession_id();
        }
        String uuid = uuidUtils.create_uuid();
        sessionMapper.insert(uuid, userId, friendId, ImSessionStatus.DISPLAY);
        sessionMapper.insert(uuid, friendId, userId, ImSessionStatus.HIDDEN);
        return uuid;
    }

    @Override
    public void update_status(Long userId, Long friendId, String sessionId, Short status) {
        Session session = sessionMapper.select_session_by_user(userId, friendId);
        if (session==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        sessionMapper.update_status(session.getId(),status);
    }
}
