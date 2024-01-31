package com.xzccc.model.Redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserToken {
    private String token;
    private Long exp;
}
