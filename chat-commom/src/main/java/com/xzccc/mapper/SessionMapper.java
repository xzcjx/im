package com.xzccc.mapper;

import com.xzccc.model.Dao.Session;
import com.xzccc.model.Vo.SessionResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SessionMapper {

    @Select("select * from im_session where user_id=#{userId} and friend_id=#{friendId}")
    Session select_session_by_user_friend(
            @Param("userId") Long userId, @Param("friendId") Long friendId);

    @Select("select * from im_session where user_id=#{userId} and session_id=#{sessionId}")
    Session select_session_by_user_session(
            @Param("userId") Long userId, @Param("sessionId") String sessionId);

    @Select("select * from im_session where user_id=#{userId} and status=#{status}")
    List<SessionResponse> select_session_by_user(
            @Param("userId") Long userId, @Param("status") Short status);

    @Insert(
            "insert into im_session(session_id,user_id,friend_id,status) values (#{sessionId},#{userId},#{friendId},#{status})")
    void insert(
            @Param("sessionId") String sessionId,
            @Param("userId") Long userId,
            @Param("friendId") Long friendId,
            @Param("status") Short status);

    @Update(
            "update im_session set status=#{status} where user_id=#{userId} and seesion_id=#{sessionId}")
    void update_status(
            @Param("userId") Long userId,
            @Param("sessionId") String sessionId,
            @Param("status") Short status);
}
