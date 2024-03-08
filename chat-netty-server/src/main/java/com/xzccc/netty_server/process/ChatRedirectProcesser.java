package com.xzccc.netty_server.process;

import com.xzccc.mapper.FriendShipMapper;
import com.xzccc.model.Redis.TokenUser;
import com.xzccc.netty.model.msg.ProtoMsg;
import com.xzccc.netty_server.session.ServerSession;
import com.xzccc.protoConvertor.ChatMessageResponseConverter;
import com.xzccc.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatRedirectProcesser implements ServerProcesser {
  @Autowired RedisUtils redisUtils;

  @Autowired ChatMessageResponseConverter chatMessageResponseConverter;

  @Autowired FriendShipMapper friendShipMapper;

  @Override
  public ProtoMsg.HeadType type() {
    return ProtoMsg.HeadType.MESSAGE_REQUEST;
  }

  @Override
  public boolean action(ServerSession session, ProtoMsg.Message proto) {
    ProtoMsg.ChatMessageRequest chatMessageRequest = proto.getChatMessageRequest();
    ProtoMsg.ChatType type = chatMessageRequest.getType();
    String sessionId = proto.getSessionId();
    if (type.equals(ProtoMsg.ChatType.CHAT)) {
      // 服务端校验是否为好友关系
      Boolean r = checkFriendShip(chatMessageRequest);
      if (r == false) {
        // 构造消息发送失败的报文
        ProtoMsg.Message response = chatMessageResponseConverter.build(proto, false, "不允许向非好友发消息");
        // 发送消息发送失败的报文
        session.writeAndFlush(response);
        return false;
      }
    }
    return true;
  }

  private Boolean checkFriendShip(ProtoMsg.ChatMessageRequest chatMessageRequest) {
    long fromId = chatMessageRequest.getFromId();
    long toId = chatMessageRequest.getToId();
    Integer r = friendShipMapper.container_by_userId_friendId(fromId, toId);
    return r == 0 ? false : true;
  }

  private Boolean check(ProtoMsg.LoginRequest loginRequest) {
    long userId = loginRequest.getUserId();
    String token = loginRequest.getToken();
    TokenUser tokenUser = redisUtils.getTokenUser(token);
    if (tokenUser == null) {
      return false;
    }
    return tokenUser.getUserId() == userId ? true : false;
  }
}
