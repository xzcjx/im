package com.xzccc.model.request;

import lombok.Data;

@Data
public class HttpSignRequest {
  private String email;
  private String code;
  private String account;
  private String username;
  private String password;
}
