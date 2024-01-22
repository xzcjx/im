package com.xzccc.server.mapper;

import com.xzccc.server.model.Dao.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
    @Insert("insert into im_user(phone,username,password_hash) values (#{phone},#{username},#{password_hash})")
//    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("select * from im_user where id=#{id}")
    User select_by_id(long id);

    @Select("select * from im_user where username=#{username}")
    User select_by_username(String username);

    @Select("select * from im_user where phone=#{phone}")
    User select_by_phone(String phone);
}
