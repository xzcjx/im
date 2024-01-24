package com.xzccc.server.controller;

import com.xzccc.server.common.BaseResponse;
import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.constant.ImSessionStatus;
import com.xzccc.server.exception.BusinessException;
import com.xzccc.server.model.Dao.User;
import com.xzccc.server.model.Vo.FriendResponse;
import com.xzccc.server.model.Vo.FriendShipRequestsResponse;
import com.xzccc.server.model.Vo.UserResponse;
import com.xzccc.server.model.request.ProcessFriendRequest;
import com.xzccc.server.server.AccountService;
import com.xzccc.server.utils.ThreadLocalUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    ThreadLocalUtils threadLocalUtils;

    @GetMapping("/user")
    public BaseResponse get_user() {
        Long userId = threadLocalUtils.get();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = accountService.get_user(userId);
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        return new BaseResponse(userResponse);
    }

    @GetMapping("/add/friend/{friendId}")
    public BaseResponse add_friend(@PathVariable("friendId") Long friendId, String ps) {
        Long userId = threadLocalUtils.get();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.add_friend(userId, friendId, ps);
        return new BaseResponse(true);
    }

    @GetMapping("/note/friend")
    public BaseResponse note_friend(Long friendId, String note) {
        Long userId = threadLocalUtils.get();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.note_friend(userId, friendId, note);
        return new BaseResponse(true);
    }

    @DeleteMapping("/delete/friend")
    public BaseResponse delete_friend(Long friendId) {
        Long userId = threadLocalUtils.get();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.delete_friend(userId, friendId);
        return new BaseResponse(true);
    }

    @PostMapping("/process/friend/request")
    public BaseResponse process_friend(ProcessFriendRequest processFriendRequest) {
        Long friendId = processFriendRequest.getFriendId();
        Long userId = threadLocalUtils.get();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.process_friend(userId, processFriendRequest);
        return new BaseResponse(true);
    }

    @GetMapping("/get/friend/requests")
    public BaseResponse get_friend_requests(@RequestParam(value = "page", defaultValue = "1") Long page,
                                            @RequestParam(value = "pagesize", defaultValue = "10") Long pagesize) {
        Long userId = threadLocalUtils.get();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<FriendShipRequestsResponse> friendRequests = accountService.get_friend_requests(userId, page, pagesize);
        return new BaseResponse(friendRequests);
    }

    @GetMapping("/get/friends")
    public BaseResponse get_friends() {
        Long userId = threadLocalUtils.get();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<FriendResponse> friends = accountService.get_friends(userId);
        return new BaseResponse(friends);
    }

    @GetMapping("/create/session/{friendId}")
    public BaseResponse create_session(@PathVariable("friendId") Long friendId) {
        Long userId = threadLocalUtils.get();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String sessionId = accountService.create_session(userId, friendId);
        return new BaseResponse(sessionId);
    }

    @GetMapping("/hidden/session")
    public BaseResponse hidden_session(Long friendId,String sessionId){
        Long userId=threadLocalUtils.get();
        if (userId==null||friendId==null||sessionId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.update_status(userId,friendId,sessionId, ImSessionStatus.HIDDEN);
        return new BaseResponse(true);
    }

    @GetMapping("/delete/session")
    public BaseResponse delete_session(Long friendId,String sessionId){
        Long userId=threadLocalUtils.get();
        if (userId==null||friendId==null||sessionId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.update_status(userId,friendId,sessionId, ImSessionStatus.DELETE);
        // delete session需要删除聊天记录
        return new BaseResponse(true);
    }
}
