package com.xzccc.utils.hash;

import org.springframework.stereotype.Component;

@Component
public class Hash512 extends HashTemplate {

    @Override
    public String hash(String msg) {
        return super.hash(msg);
    }

    @Override
    public boolean hash_verify(String msg, String hash_val) {
        return super.hash_verify(msg, hash_val);
    }
}
