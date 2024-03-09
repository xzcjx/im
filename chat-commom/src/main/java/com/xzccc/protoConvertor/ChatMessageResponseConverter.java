package com.xzccc.protoConvertor;

import com.xzccc.netty.model.msg.ProtoMsg;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageResponseConverter {
    public ProtoMsg.Message build(ProtoMsg.Message proto, boolean result, String msg) {
        ProtoMsg.ChatMessageRequest chatMessageRequest = proto.getChatMessageRequest();
        ProtoMsg.ChatMessageResponse.Builder builder = ProtoMsg.ChatMessageResponse.newBuilder();
        builder
                .setFromId(chatMessageRequest.getFromId())
                .setToId(chatMessageRequest.getToId())
                .setMsgId(chatMessageRequest.getMsgId())
                .setSendTime(chatMessageRequest.getSendTime())
                .setResult(result)
                .setMsg(msg);
        return ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.MESSAGE_RESPONSE)
                .setSessionId(proto.getSessionId())
                .setChatMessageResponse(builder.build())
                .build();
    }
}
