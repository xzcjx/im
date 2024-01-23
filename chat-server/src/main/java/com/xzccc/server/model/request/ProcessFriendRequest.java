package com.xzccc.server.model.request;

import lombok.Data;

@Data
public class ProcessFriendRequest {
    private Boolean agree;
    private Long friendId;
}
