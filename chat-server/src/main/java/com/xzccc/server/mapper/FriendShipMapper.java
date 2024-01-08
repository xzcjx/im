package com.xzccc.server.mapper;

import com.xzccc.server.model.Dao.FriendShip;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestParam;

@Mapper
public interface FriendShipMapper {

    @Select("select * from friendship where uid=#{uid} and friend_id=#{friend_id}")
    FriendShip select_by_uid_friend_id(Long uid, Long friend_id);

    @Insert("insert into friendship(uid,friend_id) values (#{uid},#{friend_id})")
    void insert(Long uid, Long friend_id);

    @Update("update friendship set note=#{note} where uid=#{uid} and friend_id=#{friend_id}")
    void update_note(Long uid, Long friendId, String note);
}
