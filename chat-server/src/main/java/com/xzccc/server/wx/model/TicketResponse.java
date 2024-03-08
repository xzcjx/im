package com.xzccc.server.wx.model;

import lombok.Data;

@Data
public class TicketResponse {
  private String ticket;
  private String url;
  private long expire_seconds;
}
