package com.xzccc.server.mapper;

import com.xzccc.server.model.Dao.FriendShip;
import com.xzccc.server.model.Vo.FriendResponse;
import com.xzccc.server.model.Vo.FriendShipRequestsResponse;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface FriendShipMapper {

    @Select("select * from im_relationship where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    FriendShip select_by_userId_friendId(@Param(value = "userId") Long userId, @Param(value = "friendId") Long friendId);

    @Select("select count(*) from im_relationship where user_id=#{userId} and status>=3 and deleted_at is null")
    Long count_by_userId(@Param("userId") Long userId);

    @Insert("insert into im_relationship(user_id,friend_id,status,ps) values (#{userId},#{friendId},#{status},#{ps})")
    void insert(@Param(value = "userId") Long userId,
                @Param(value = "friendId") Long friendId,
                @Param(value = "status") Short status,
                @Param(value = "ps") String ps);

    @Update("update im_relationship set note=#{note} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void update_note(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("note") String note);

    @Update("update im_relationship set status=#{status} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void update_status(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") Short status);

    @Update("update im_relationship set deleted_at=#{deleted_at} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void delete(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("deleted_at") Date deleted_at);

    @Select("select friend_id,status,ps,created_at from im_relationship where user_id=#{userId} limit #{offset},#{pagesize}")
    List<FriendShipRequestsResponse> page_by_userId(@Param("userId") Long userId, @Param("offset") long offset, @Param("pagesize") Long pagesize);

    @Select("select u.id,u.username,r.note from im_relationship r join im_user u on r.user_id=#{userId} and r.status>=3 and r.friend_id=u.id")
    List<FriendResponse> select_friend_info(Long userId);
}
