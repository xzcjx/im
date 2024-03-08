package com.xzccc.model.Vo;

import java.util.Date;
import lombok.Data;

@Data
public class FriendShipRequestsResponse {
  private Long friendId;
  private Short read;
  private Short status;
  private String ps;
  private Date createdAt;
}
