package com.xzccc.server.controller;

import com.xzccc.server.common.BaseResponse;
import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.exception.BusinessException;
import com.xzccc.server.model.Dao.User;
import com.xzccc.server.model.Vo.UserResponse;
import com.xzccc.server.server.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping("/user")
    public BaseResponse get_user(HttpServletRequest request) {
        Long uid = (Long) request.getSession().getAttribute("uid");
        if (uid==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user=accountService.get_user(uid);
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user,userResponse);
        return new BaseResponse(userResponse);
    }

    @GetMapping("/add/friend/{friend_id}")
    public BaseResponse add_friend(HttpServletRequest request,@PathVariable("friend_id")Long friend_id){
        Long uid = (Long) request.getSession().getAttribute("uid");
        if (uid==null||friend_id==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.add_friend(uid,friend_id);
        return new BaseResponse(true);
    }

    @GetMapping("/note/friend")
    public BaseResponse note_friend(HttpServletRequest request,Long friend_id,String note){
        Long uid = (Long) request.getSession().getAttribute("uid");
        if (uid==null||friend_id==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        accountService.note_friend(uid,friend_id,note);
        return new BaseResponse(true);
    }
}
