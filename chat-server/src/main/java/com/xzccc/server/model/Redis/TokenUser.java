package com.xzccc.server.model.Redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenUser {
    private long user_id;
    private Long exp;
}
