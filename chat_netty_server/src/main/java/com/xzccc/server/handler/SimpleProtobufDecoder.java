package com.xzccc.server.handler;

import com.xzccc.server.constant.ProtoInstant;
import com.xzccc.server.exception.InvalidFrameException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class SimpleProtobufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        Object outmsg=deocde0(ctx,in);
    }

    private  Object deocde0(ChannelHandlerContext ctx, ByteBuf in) throws InvalidFrameException {
        // 标记当前readIndex的位置
        if(in.readableBytes()<8){
            //不够包头
            return null;
        }
        // 读取魔数
        short magic = in.readShort();
        if (magic!= ProtoInstant.MAGIC_CODE) {
            String error ="客户端口令不对: "+ctx.channel().remoteAddress();
            throw new InvalidFrameException(error);
        }
        return null;
    }
}
