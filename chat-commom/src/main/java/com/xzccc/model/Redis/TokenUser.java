package com.xzccc.model.Redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenUser {
    private long userId;
    private Long exp;
}
