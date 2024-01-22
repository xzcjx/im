package com.xzccc.server.mapper;

import com.xzccc.server.model.Dao.FriendShip;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Mapper
public interface FriendShipMapper {

    @Select("select * from im_relationship where user_id=#{userId} and friend_id=#{friendId} and deleted_at is null")
    FriendShip select_by_user_id_friend_id(@Param(value = "userId") Long userId, @Param(value = "friendId") Long friendId);

    @Insert("insert into im_relationship(sponsor,user_id,friend_id,status,ps) values (#{sponsor},#{userId},#{friendId},#{status},#{ps})")
    void insert(@Param(value = "sponsor") Short sponsor,
                @Param(value = "userId") Long userId,
                @Param(value = "friendId") Long friendId,
                @Param(value = "status") Short status,
                @Param(value = "ps") String ps);

    @Update("update im_relationship set note=#{note} where user_id=#{userId} and friend_id=#{friendId}")
    void update_note(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("note") String note);

    @Update("update im_relationship set status=#{status} where user_id=#{userId} and friend_id=#{friendId}")
    void update_status(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") Short status);

    @Update("update im_relationship set deleted_at=#{deleted_at} where user_id=#{userId} and friend_id=#{friendId}")
    void delete(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("deleted_at") Date deleted_at);
}
