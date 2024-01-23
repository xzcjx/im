package com.xzccc.server.mapper;

import com.xzccc.server.model.Dao.Session;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SessionMapper {

    @Select("select * from im_session where user_id=#{userId} and friend_id=#{friendId}")
    Session select_session_by_user(Long userId,Long friendId);

    @Insert("insert into im_session(session_id,user_id,friend_id,status) values (#{sessionId},#{userId},#{friendId},#{status})")
    void insert(String sessionId,Long userId,Long friendId,Short status);
}
