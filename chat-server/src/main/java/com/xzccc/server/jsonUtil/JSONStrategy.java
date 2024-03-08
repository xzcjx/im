package com.xzccc.server.jsonUtil;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JSONStrategy {
  @Autowired FastJsonStrategy fastJsonStrategy;

  public String toJson(Object object, String strategy) {
    if (strategy.equals("fastjson")) {
      return fastJsonStrategy.toJson(object);
    }
    return null;
  }

  public <T> T fromJson(String string, String strategy, Class<T> tClass) {
    if (strategy.equals("fastjson")) {
      return fastJsonStrategy.fromJson(string, tClass);
    }
    return null;
  }

  public Object fromJson(String string, String strategy) {
    if (strategy.equals("fastjson")) {
      return fastJsonStrategy.fromJson(string);
    }
    return null;
  }

  public String fastjsonToJson(Object object) {
    return toJson(object, "fastjson");
  }

  public <T> T fastjsonFromJson(String string, Class<T> tClass) {
    return fromJson(string, "fastjson", tClass);
  }

  public Object fastjsonFromJson(String string) {
    return fromJson(string, "fastjson");
  }
}
