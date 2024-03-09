package com.xzccc.model.Vo;

import lombok.Data;

import java.util.Date;

@Data
public class FriendShipRequestsResponse {
    private Long friendId;
    private Short read;
    private Short status;
    private String ps;
    private Date createdAt;
}
