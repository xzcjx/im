package com.xzccc.mapper;

import com.xzccc.model.Dao.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
  @Insert(
      "insert into im_user(email,account,username,password_hash,status) "
          + "values (#{email},#{account},#{username},#{password_hash},#{status})")
  //    @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(User user);

  @Select("select * from im_user where id=#{id} and deleted_at is null")
  User select_by_id(long id);

  @Select("select * from im_user where username=#{username}")
  User select_by_username(String username);

  @Select("select * from im_user where email=#{email} and deleted_at is null")
  User select_by_email(String email);

  @Select("select * from im_user where account=#{account} and deleted_at is null")
  User select_by_account(String account);

  @Update("update im_user set username=#{username} where id=#{userId} and deleted_at is null")
  void update_username(Long userId, String username);
}
