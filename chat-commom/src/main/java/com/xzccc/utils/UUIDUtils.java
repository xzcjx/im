package com.xzccc.utils;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UUIDUtils {
    private String nameUUID="im_session";
    public String create_uuid(){
        UUID uuid = UUID.nameUUIDFromBytes(nameUUID.getBytes());
        return uuid.toString();
    }
}
