package com.xzccc.server.controller;

import com.xzccc.server.common.BaseResponse;
import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.exception.BusinessException;
import com.xzccc.server.model.Dao.User;
import com.xzccc.server.model.Vo.UserResponse;
import com.xzccc.server.server.AccountService;
import com.xzccc.server.utils.ThreadLocalUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    @GetMapping("/add/friend/{friend_id}")
    public BaseResponse add_friend(@PathVariable("friend_id")Long friend_id,String ps){
        Long userId = threadLocalUtils.get();
        if (userId==null||friend_id==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.add_friend(userId,friend_id,ps);
        return new BaseResponse(true);
    }

    @GetMapping("/note/friend")
    public BaseResponse note_friend(Long friend_id,String note){
        Long userId = threadLocalUtils.get();
        if (userId==null||friend_id==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.note_friend(userId,friend_id,note);
        return new BaseResponse(true);
    }

    @DeleteMapping("/delete/friend")
    public BaseResponse delete_friend(Long friend_id){
        Long userId = threadLocalUtils.get();
        if (userId==null||friend_id==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.delete_friend(userId,friend_id);
        return new BaseResponse(true);
    }
}
