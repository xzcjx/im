package com.xzccc.server.mapper;

import com.xzccc.server.model.Dao.Session;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SessionMapper {

    @Select("select * from im_session where user_id=#{userId} and friend_id=#{friendId}")
    Session select_session_by_user(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Insert("insert into im_session(session_id,user_id,friend_id,status) values (#{sessionId},#{userId},#{friendId},#{status})")
    void insert(@Param("sessionId") String sessionId, @Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") Short status);

    @Update("update im_session set status=#{status} where id=#{id}")
    void update_status(@Param("id") Long id,@Param("status") Short status);
}
