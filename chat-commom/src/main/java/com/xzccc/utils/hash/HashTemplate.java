package com.xzccc.utils.hash;

import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashTemplate {

    @Value("${hash.salt}")
    private String salt;

    private String algorithm = "SHA-512";

    public String hash(String msg) {
        return hash(msg, salt, algorithm, false);
    }

    public String hash(String msg, String algorithm) {
        return hash(msg, salt, algorithm, false);
    }

    public boolean hash_verify(String msg, String hash_val) {
        if (hash(msg).equals(hash_val)) {
            return true;
        }
        return false;
    }

    public String hash(String msg, String salt, String algorithm, boolean uppercase) {
        byte[] sqlt_byte = salt.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(sqlt_byte);
            byte[] hashed = md.digest(msg.getBytes(StandardCharsets.UTF_8));
            String result = byteArrayToHexString(hashed);
            return uppercase ? result.toUpperCase() : result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            //java.lang.Integer.toHexString() 方法的参数是int(32位)类型，
            //如果输入一个byte(8位)类型的数字，这个方法会把这个数字的高24为也看作有效位，就会出现错误
            //如果使用& 0XFF操作，可以把高24位置0以避免这样错误
            String temp = Integer.toHexString(b & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                builder.append("0");
            }
            builder.append(temp);
        }
        return builder.toString();
    }


}
