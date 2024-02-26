package com.xzccc.server.wx.impl;

import com.xzccc.server.wx.model.AccessTokenResponse;
import com.xzccc.server.wx.model.TicketResponse;

public class IWxLogin implements com.xzccc.server.wx.IWxLogin {
    @Override
    public AccessTokenResponse get_access_token() {
        return null;
    }

    @Override
    public TicketResponse get_ticket(String access_token) {
        return null;
    }
}
