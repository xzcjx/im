package com.xzccc.server.utils;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenUtils {

    public String getUUID32(){
        return UUID.randomUUID().toString().toLowerCase(); //.replace("-", "")
    }
}
