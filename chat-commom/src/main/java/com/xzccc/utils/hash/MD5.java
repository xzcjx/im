package com.xzccc.utils.hash;

import org.springframework.stereotype.Component;

@Component
public class MD5 extends HashTemplate {
    private String algorithm = "MD5";

    @Override
    public String hash(String msg) {
        return hash(msg, algorithm);
    }

    @Override
    public boolean hash_verify(String msg, String hash_val) {
        return super.hash_verify(msg, hash_val);
    }
}
