package com.xzccc.server.impl;


import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.xzccc.common.ErrorCode;
import com.xzccc.constant.ImFriendRelationshipStatus;
import com.xzccc.constant.ImSessionStatus;
import com.xzccc.constant.RedisConstant;
import com.xzccc.exception.BusinessException;
import com.xzccc.mapper.FriendShipInfoMapper;
import com.xzccc.mapper.FriendShipMapper;
import com.xzccc.mapper.SessionMapper;
import com.xzccc.mapper.UserMapper;
import com.xzccc.model.Dao.FriendShip;
import com.xzccc.model.Dao.FriendShipInfo;
import com.xzccc.model.Dao.Session;
import com.xzccc.model.Dao.User;
import com.xzccc.model.Redis.UserToken;
import com.xzccc.model.Vo.FriendResponse;
import com.xzccc.model.Vo.FriendShipRequestsResponse;
import com.xzccc.model.Vo.FriendStatusResponse;
import com.xzccc.model.request.ProcessFriendRequest;
import com.xzccc.server.AccountService;
import com.xzccc.utils.RedisUtils;
import com.xzccc.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
    FriendShipInfoMapper friendShipInfoMapper;

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

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    SensitiveWordBs sensitiveWordBs;

    @Override
    public User get_user(long id) {
        return userMapper.select_by_id(id);
    }

    @Override
    public void add_friend(Long userId, Long friendId, String ps) {
        if (StringUtils.isBlank(ps) || ps.length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (sensitiveWordBs.contains(ps)) {
            throw new BusinessException(ErrorCode.SENSITIVE_ERROR);
        }
        Long count = friendShipMapper.count_by_userId(userId);
        if (count >= friendLimit) {
            throw new BusinessException(ErrorCode.FRIEND_LIMIT);
        }
        FriendShip friendShip = friendShipMapper.select_by_userId_friendId(userId, friendId);
        if (friendShip != null) {
            throw new BusinessException(ErrorCode.FRIEND_EXISTS);
        }
        friendShipInfoMapper.insert(userId, friendId, ImFriendRelationshipStatus.READ, ImFriendRelationshipStatus.SPONSOR, ps);
        friendShipInfoMapper.insert(friendId, userId, ImFriendRelationshipStatus.UNREAD, ImFriendRelationshipStatus.NOT_PROCESSED, ps);
        // 此处需要通过websocket通知对方，如果对方在线
    }

    @Override
    public void note_friend(Long userId, Long friendId, String note) {
        if (StringUtils.isBlank(note) || note.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
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
            throw new BusinessException(ErrorCode.FRIEND_NOT_EXISTS);
        }
        friendShipMapper.delete(userId, friendId, new Date());
    }

    @Override
    public void process_friend(Long userId, ProcessFriendRequest processFriendRequest) {
        Long friendId = processFriendRequest.getFriendId();
        Short status = processFriendRequest.getStatus();
        if (status == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 这里可能有bug，接收者的被删除后，再进行处理是不被允许的
        FriendShipInfo friendShipInfo = friendShipInfoMapper.select_by_userId_friendId(userId, friendId);
        if (friendShipInfo == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        switch (status) {
            case 1:
                Long count = friendShipMapper.count_by_userId(userId);
                if (count >= friendLimit) {
                    throw new BusinessException(ErrorCode.FRIEND_LIMIT);
                }
                friendShipInfoMapper.update(userId, friendId, ImFriendRelationshipStatus.READ,
                        ImFriendRelationshipStatus.AGREE);
                friendShipInfoMapper.update_status(friendId, userId, ImFriendRelationshipStatus.AGREE);
                friendShipMapper.insert(userId, friendId);
                friendShipMapper.insert(friendId, userId);
            case 2:
                friendShipInfoMapper.update(userId, friendId, ImFriendRelationshipStatus.READ,
                        ImFriendRelationshipStatus.REJECT);
                friendShipInfoMapper.update_status(friendId, userId, ImFriendRelationshipStatus.REJECT);
            case 3:
                friendShipInfoMapper.update(userId, friendId, ImFriendRelationshipStatus.READ,
                        ImFriendRelationshipStatus.IGNORE);
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public List<FriendShipRequestsResponse> get_friend_requests(Long userId, Long page, Long pagesize) {
        List<FriendShipRequestsResponse> friendShipList = friendShipInfoMapper.page_by_userId(userId, (page - 1) * pagesize, pagesize);
        return friendShipList;
    }

    @Override
    public List<FriendResponse> get_friends(Long userId) {
        List<FriendResponse> friendResponses = friendShipMapper.select_friend_info(userId);
        return friendResponses;
    }

    @Override
    public String create_session(Long userId, Long friendId) {
        FriendShip friendShip = friendShipMapper.select_by_userId_friendId(userId, friendId);
        if (friendShip == null) {
            throw new BusinessException(ErrorCode.FRIEND_NOT_EXISTS);
        }
        Session session = sessionMapper.select_session_by_user(userId, friendId);
        if (session != null) {
            if (session.getStatus() != ImSessionStatus.DISPLAY) {
                sessionMapper.update_status(session.getId(), ImSessionStatus.DISPLAY);
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
        if (session == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        sessionMapper.update_status(session.getId(), status);
    }

    @Override
    public List<FriendStatusResponse> get_friend_status(Long userId) {
        List<FriendStatusResponse> friendStatusResponseList = friendShipMapper.select_friend_ids(userId);
        for (FriendStatusResponse friend :
                friendStatusResponseList) {
            Long id = friend.getId();
            Boolean b = redisUtils.containerUserStatus(id);
            if (b.equals(true))
                friend.setStatus(true);
            else
                friend.setStatus(false);
        }
        return friendStatusResponseList;
    }

    @Override
    public void read_friend(Long userId) {
        friendShipInfoMapper.update_by_user(userId, ImFriendRelationshipStatus.READ);
    }

    @Override
    public void update_username(Long userId, String username) {
        if (StringUtils.isBlank(username) || username.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (sensitiveWordBs.contains(username)) {
            throw new BusinessException(ErrorCode.SENSITIVE_ERROR);
        }
        userMapper.update_username(userId, username);
    }
}
