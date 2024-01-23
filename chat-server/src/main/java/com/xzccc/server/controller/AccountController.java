package com.xzccc.server.controller;

import com.xzccc.server.common.BaseResponse;
import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.exception.BusinessException;
import com.xzccc.server.model.Dao.User;
import com.xzccc.server.model.Vo.UserResponse;
import com.xzccc.server.model.request.ProcessFriendRequest;
import com.xzccc.server.server.AccountService;
import com.xzccc.server.utils.ThreadLocalUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.TreeMap;

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
        if (userId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user=accountService.get_user(userId);
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user,userResponse);
        return new BaseResponse(userResponse);
    }

    @GetMapping("/add/friend/{friendId}")
    public BaseResponse add_friend(@PathVariable("friendId")Long friendId,String ps){
        Long userId = threadLocalUtils.get();
        if (userId==null||friendId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.add_friend(userId,friendId,ps);
        return new BaseResponse(true);
    }

    @GetMapping("/note/friend")
    public BaseResponse note_friend(Long friendId,String note){
        Long userId = threadLocalUtils.get();
        if (userId==null||friendId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.note_friend(userId,friendId,note);
        return new BaseResponse(true);
    }

    @DeleteMapping("/delete/friend")
    public BaseResponse delete_friend(Long friendId){
        Long userId = threadLocalUtils.get();
        if (userId==null||friendId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.delete_friend(userId,friendId);
        return new BaseResponse(true);
    }

    @PostMapping("/process/friend/request")
    public BaseResponse process_friend(ProcessFriendRequest processFriendRequest){
        Long friendId = processFriendRequest.getFriendId();
        Long userId=threadLocalUtils.get();
        if (userId==null||friendId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.process_friend(userId,processFriendRequest);
        return new BaseResponse(true);
    }

    @GetMapping("/create/session/{friendId}")
    public BaseResponse create_session(@PathVariable("friendId")Long friendId){
        Long userId=threadLocalUtils.get();
        if (userId==null||friendId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String sessionId=accountService.create_session(userId,friendId);
        return new BaseResponse(true);
    }

    @GetMapping("/get/session/{friendId}")
    public BaseResponse get_session(@PathVariable("friendId")Long friendId){
        Long userId=threadLocalUtils.get();
        if (userId==null||friendId==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String sessionId=accountService.get_session(userId,friendId);
        return new BaseResponse(true);
    }
}
