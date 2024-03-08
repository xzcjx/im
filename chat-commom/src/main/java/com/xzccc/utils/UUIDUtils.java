package com.xzccc.utils;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UUIDUtils {
  //  private String nameUUID = "im_session";

  public String create_uuid() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }
}
