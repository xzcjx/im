package com.xzccc.server.mapper;

import com.xzccc.server.model.Dao.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
    @Insert("insert into user(phone,username,password_hash) values (#{phone},#{username},#{password_hash})")
    void insert(@Param("phone") String phone, @Param("username") String username, @Param("password_hash") String password_hash);

    @Select("select * from user where id=#{id}")
    User select_by_id(long id);

    @Select("select * from user where username=#{username}")
    User select_by_username(String username);

    @Select("select * from user where phone=#{phone}")
    User select_by_phone(String phone);
}
