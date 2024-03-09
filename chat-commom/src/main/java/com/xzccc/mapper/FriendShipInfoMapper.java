package com.xzccc.mapper;

import com.xzccc.model.Dao.FriendShipInfo;
import com.xzccc.model.Vo.FriendShipRequestsResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendShipInfoMapper {

    @Insert(
            "insert into im_friend_relationship_info(user_id,friend_id,`read`,status,ps) values (#{userId},#{friendId},#{read},#{status},#{ps})")
    void insert(
            @Param("userId") Long userId,
            @Param("friendId") Long friendId,
            @Param("read") Short read,
            @Param("status") Short status,
            @Param("ps") String ps);

    @Select(
            "select friend_id,`read`,status,ps,created_at from im_friend_relationship_info where user_id=#{userId} and deleted_at is null limit #{offset},#{pagesize}")
    List<FriendShipRequestsResponse> page_by_userId(
            @Param("userId") Long userId, @Param("offset") long offset, @Param("pagesize") Long pagesize);

    @Update(
            "update im_friend_relationship_info set status=#{status} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void update_status(
            @Param("userId") Long userId,
            @Param("friendId") Long friendId,
            @Param("status") Short status);

    @Update(
            "update im_friend_relationship_info set status=#{status},`read`=#{read} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void update(
            @Param("userId") Long userId,
            @Param("friendId") Long friendId,
            @Param("read") Short read,
            @Param("status") Short status);

    @Update(
            "update im_friend_relationship_info set `read`=#{read} where user_id=#{userId} and `read`=0 and deleted_at is null")
    void update_by_user(@Param("userId") Long userId, @Param("read") Short read);

    @Select(
            "select * from im_friend_relationship_info where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    FriendShipInfo select_by_userId_friendId(
            @Param("userId") Long userId, @Param("friendId") Long friendId);
}
