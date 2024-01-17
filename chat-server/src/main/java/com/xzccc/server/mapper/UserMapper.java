package com.xzccc.server.mapper;

import com.xzccc.server.model.Dao.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
    @Insert("insert into user(phone,username,password_hash) values (#{phone},#{username},#{password_hash})")
//    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("select * from user where id=#{id}")
    User select_by_id(long id);

    @Select("select * from user where username=#{username}")
    User select_by_username(String username);

    @Select("select * from user where phone=#{phone}")
    User select_by_phone(String phone);
}
