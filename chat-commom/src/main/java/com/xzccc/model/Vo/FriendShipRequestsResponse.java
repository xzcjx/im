package com.xzccc.model.Vo;

import lombok.Data;

import java.util.Date;

@Data
public class FriendShipRequestsResponse {
    private Long friend_id;
    private Short read;
    private Short status;
    private String ps;
    private Date created_at;
}
