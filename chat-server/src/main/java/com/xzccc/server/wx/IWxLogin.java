package com.xzccc.server.wx;

import com.xzccc.server.wx.model.AccessTokenResponse;
import com.xzccc.server.wx.model.TicketResponse;

public interface IWxLogin {
    AccessTokenResponse get_access_token();

    TicketResponse get_ticket(String access_token);
}
