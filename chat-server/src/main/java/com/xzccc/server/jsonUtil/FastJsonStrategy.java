package com.xzccc.server.jsonUtil;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FastJsonStrategy implements IJSONConverter {
    @Override
    public String toJson(Object object) {
        return JSONObject.toJSONString(object);
    }

    @Override
    public Object fromJson(String string) {
        return JSONObject.parseObject(string);
    }

    @Override
    public <T> T fromJson(String string, Class<T> tClass) {
        return JSONObject.parseObject(string, tClass);
    }
}
