package com.xzccc.mapper;

import com.xzccc.model.Dao.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("insert into im_user(email,account,username,password_hash,status) " +
            "values (#{email},#{account},#{username},#{password_hash},#{status})")
//    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("select * from im_user where id=#{id}")
    User select_by_id(long id);

    @Select("select * from im_user where username=#{username}")
    User select_by_username(String username);

    @Select("select * from im_user where email=#{email}")
    User select_by_email(String email);
    @Select("select * from im_user where account=#{account}")
    User select_by_account(String account);
}
