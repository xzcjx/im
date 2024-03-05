package com.xzccc.mapper;

import com.xzccc.model.Dao.FriendShip;
import com.xzccc.model.Vo.FriendResponse;
import com.xzccc.model.Vo.FriendShipRequestsResponse;
import com.xzccc.model.Vo.FriendStatusResponse;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface FriendShipMapper {

    @Select("select * from im_friend_relationship where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    FriendShip select_by_userId_friendId(@Param(value = "userId") Long userId, @Param(value = "friendId") Long friendId);

    @Select("select count(*) from im_friend_relationship where user_id=#{userId} and deleted_at is null")
    Long count_by_userId(@Param("userId") Long userId);

    @Insert("insert into im_friend_relationship(user_id,friend_id) values (#{userId},#{friendId})")
    void insert(@Param(value = "userId") Long userId,
                @Param(value = "friendId") Long friendId);

    @Update("update im_friend_relationship set note=#{note} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void update_note(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("note") String note);

    @Update("update im_friend_relationship set status=#{status} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void update_status(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") Short status);

    @Update("update im_friend_relationship set deleted_at=#{deleted_at} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void delete(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("deleted_at") Date deleted_at);

    @Select("select friend_id,status,ps,created_at from im_friend_relationship where user_id=#{userId} limit #{offset},#{pagesize}")
    List<FriendShipRequestsResponse> page_by_userId(@Param("userId") Long userId, @Param("offset") long offset, @Param("pagesize") Long pagesize);

    @Select("select u.id,u.username,r.note from im_friend_relationship r join im_user u on r.user_id=#{userId} and r.delete_at is null and r.friend_id=u.id")
    List<FriendResponse> select_friend_info(Long userId);

    @Select("select r.id from im_friend_relationship r where r.user_id=#{userId} and r.status>=3")
    List<FriendStatusResponse> select_friend_ids(Long userId);

    @Select("select count(*) from im_friend_relationship where user_id=#{userId} and friend_id=#{friendId} and status>=3 and deleted_at is null")
    Integer container_by_userId_friendId(long userId, long friendId);
}
