package com.xzccc.server.jsonUtil;

public interface IJSONConverter {
    String toJson(Object object);
    Object fromJson(String string);
    <T>T fromJson(String string,Class<T> tClass);
}
