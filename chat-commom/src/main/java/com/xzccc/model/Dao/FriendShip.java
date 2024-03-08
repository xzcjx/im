package com.xzccc.model.Dao;

import java.util.Date;
import lombok.Data;

@Data
public class FriendShip {
  private Long id;
  private Long user_id;
  private Long friend_id;
  private Short status;
  private String ps;
  private String note;
  private Date created_at;
  private Date deleted_at;
}
