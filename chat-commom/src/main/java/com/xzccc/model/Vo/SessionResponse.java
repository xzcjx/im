package com.xzccc.model.Vo;

import lombok.Data;

@Data
public class SessionResponse {
  private String sessionId;
  private Long userId;
  private Long friendId;
}
