package com.xzccc.utils;


import com.xzccc.constant.HashUtilsConstant;
import com.xzccc.utils.hash.Hash512;
import com.xzccc.utils.hash.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HashUtils {
    @Autowired
    private Hash512 hash512utils;

    @Autowired
    private MD5 md5utils;

    public String DefaultHash(String msg){
        return hash512utils.hash(msg);
    }

    public boolean DefaultVerify(String msg,String msg_hash){
        return hash512utils.hash_verify(msg,msg_hash);
    }

    public String SelectHash(String msg,String algorithm){
        if(algorithm.equals(HashUtilsConstant.hash512_utils)){
            return hash512utils.hash(msg);
        }else if (algorithm.equals(HashUtilsConstant.md5_utils)){
            return md5utils.hash(msg);
        }
        return null;
    }

    public boolean SelectVerify(String msg,String msg_hash,String algorithm){
        if(algorithm.equals(HashUtilsConstant.hash512_utils)){
            return hash512utils.hash_verify(msg,msg_hash);
        }else if (algorithm.equals(HashUtilsConstant.md5_utils)){
            return md5utils.hash_verify(msg,msg_hash);
        }
        return false;
    }

}
