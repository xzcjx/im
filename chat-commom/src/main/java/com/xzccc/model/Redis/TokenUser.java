package com.xzccc.model.Redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenUser {
    private long userId;
    private Long exp;
}
