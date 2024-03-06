package com.xzccc.server.controller;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.xzccc.common.BaseResponse;
import com.xzccc.common.ErrorCode;
import com.xzccc.constant.ImSessionStatus;
import com.xzccc.exception.BusinessException;
import com.xzccc.model.Dao.User;
import com.xzccc.model.Vo.FriendResponse;
import com.xzccc.model.Vo.FriendShipRequestsResponse;
import com.xzccc.model.Vo.FriendStatusResponse;
import com.xzccc.model.Vo.UserResponse;
import com.xzccc.model.request.*;
import com.xzccc.server.AccountService;

import com.xzccc.utils.ThreadLocalUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${app.config.api-version}/account")
@Api(tags = {"涉及用户相关的api"})
public class AccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    ThreadLocalUtils threadLocalUtils;


    @GetMapping("/user")
    @ApiOperation(value = "获取自己的基本信息")
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

    @PostMapping("/update/username")
    @ApiOperation(value = "修改用户名")
    public BaseResponse update_username(@RequestBody UpdateUsernameRequest updateUsernameRequest) {
        Long userId = threadLocalUtils.get();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String username = updateUsernameRequest.getUsername();
        accountService.update_username(userId,username);
        return new BaseResponse(true);
    }

    @GetMapping("/get/friends")
    @ApiOperation(value = "获取所有好友信息")
    public BaseResponse get_friends() {
        Long userId = threadLocalUtils.get();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<FriendResponse> friends = accountService.get_friends(userId);
        return new BaseResponse(friends);
    }

    @PostMapping("/add/friend")
    @ApiOperation(value = "添加好友")
    public BaseResponse add_friend(@RequestBody AddFriendRequest addFriendRequest) {
        Long userId = threadLocalUtils.get();
        Long friendId = addFriendRequest.getFriendId();
        String ps = addFriendRequest.getPs();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.add_friend(userId, friendId, ps);
        return new BaseResponse(true);
    }

    @GetMapping("/get/friend/requests")
    @ApiOperation(value = "获取好友申请数据")
    public BaseResponse get_friend_requests(@RequestParam(value = "page", defaultValue = "1") Long page,
                                            @RequestParam(value = "pagesize", defaultValue = "10") Long pagesize) {
        Long userId = threadLocalUtils.get();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<FriendShipRequestsResponse> friendRequests = accountService.get_friend_requests(userId, page, pagesize);
        return new BaseResponse(friendRequests);
    }

    @DeleteMapping("/delete/friend")
    @ApiOperation(value = "单方面删除好友")
    public BaseResponse delete_friend(@RequestBody DeleteFriendRequest deleteFriendRequest) {
        Long friendId = deleteFriendRequest.getFriendId();
        Long userId = threadLocalUtils.get();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.delete_friend(userId, friendId);
        return new BaseResponse(true);
    }

    @GetMapping("/note/friend")
    @ApiOperation(value = "为好友设置或修改备注")
    public BaseResponse note_friend(@RequestBody NoteFriendRequest noteFriendRequest) {
        Long userId = threadLocalUtils.get();
        Long friendId = noteFriendRequest.getFriendId();
        String note = noteFriendRequest.getNote();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.note_friend(userId, friendId, note);
        return new BaseResponse(true);
    }

    @PostMapping("/process/friend/request")
    @ApiOperation(value = "处理好友请求申请")
    public BaseResponse process_friend(@RequestBody ProcessFriendRequest processFriendRequest) {
        Long friendId = processFriendRequest.getFriendId();
        Long userId = threadLocalUtils.get();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.process_friend(userId, processFriendRequest);
        return new BaseResponse(true);
    }

    @PostMapping("/read/friend/request")
    @ApiOperation(value = "好友请求全部已读")
    public BaseResponse read_friend() {
        Long userId = threadLocalUtils.get();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.read_friend(userId);
        return new BaseResponse(true);
    }

    @GetMapping("/get/friend/status")
    @ApiOperation(value = "获取好友在线状态")
    public BaseResponse get_friend_status(){
        Long userId = threadLocalUtils.get();
        List<FriendStatusResponse> friendStatusResponseList=accountService.get_friend_status(userId);
        return new BaseResponse(friendStatusResponseList);
    }




    @GetMapping("/create/session/{friendId}")
    @ApiOperation(value = "创建好友会话")
    public BaseResponse create_session(@PathVariable("friendId") Long friendId) {
        Long userId = threadLocalUtils.get();
        if (userId == null || friendId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String sessionId = accountService.create_session(userId, friendId);
        return new BaseResponse(sessionId);
    }

    @GetMapping("/hidden/session")
    @ApiOperation(value = "不显示好友会话")
    public BaseResponse hidden_session(Long friendId,String sessionId){
        Long userId=threadLocalUtils.get();
        if (userId==null||friendId==null||sessionId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.update_status(userId,friendId,sessionId, ImSessionStatus.HIDDEN);
        return new BaseResponse(true);
    }

    @GetMapping("/delete/session")
    @ApiOperation(value = "删除好友会话，并删除聊天记录")
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
