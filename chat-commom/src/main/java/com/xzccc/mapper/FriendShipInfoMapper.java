package com.xzccc.mapper;

import com.xzccc.model.Dao.FriendShip;
import com.xzccc.model.Vo.FriendResponse;
import com.xzccc.model.Vo.FriendShipRequestsResponse;
import com.xzccc.model.Vo.FriendStatusResponse;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface FriendShipInfoMapper {


    @Insert("insert into im_friend_relationship_info(user_id,friend_id,read,status,ps) values (#{userId},#{friendId},#{read},#{status},#{ps})")
    void insert(Long userId, Long friendId, Short read, Short status, String ps);

    @Select("select friend_id,read,status,ps,created_at from im_friend_relationship_info where user_id=#{userId} and deleted_at is null limit #{offset},#{pagesize}")
    List<FriendShipRequestsResponse> page_by_userId(Long userId, long offset, Long pagesize);

    @Update("update im_friend_relationship_info set status=#{status} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void update_status(Long userId, Long friendId, Short status);

    @Update("update im_friend_relationship_info set status=#{status},read=#{read} where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    void update(Long userId, Long friendId,Short read, Short status);

    @Update("update im_friend_relationship_info set read=#{read} where user_id=#{userId} and deleted_at is null")
    void update_by_user(Long userId, Short read);
}
