package com.xzccc.utils;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TokenUtils {

  public String getUUID32() {
    return UUID.randomUUID().toString().toLowerCase(); // .replace("-", "")
  }

  public String getToken() {
    return getUUID32();
  }

  public String getSessionId() {
    return getUUID32();
  }
}
