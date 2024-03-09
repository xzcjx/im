package com.xzccc.netty_client.client;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.xzccc.netty.model.User;
import com.xzccc.netty.model.msg.ProtoMsg;
import com.xzccc.protoConvertor.LoginRequestConverter;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

@Service("ChatServer")
@Slf4j
public class ChatClient {

    @Value("${server.websocket-port}")
    private int port;

    private EventLoopGroup boss;
    private NioEventLoopGroup work;

    public static void main(String[] args) {
    }

    public void run() throws URISyntaxException {

        final URI webSocketURL =
                new URI("ws://127.0.0.1:" + port + "/im?token=9be5ccdd-edc8-43f8-aaad-36ee20f34dd2");

        Bootstrap bg = new Bootstrap();

        try {
            work = new NioEventLoopGroup(1);

            bg.group(work);

            // 设置Nio类型的Channel
            bg.channel(NioSocketChannel.class);
            //            bg.handler(new LoggingHandler(LogLevel.DEBUG));

            // 设置通道选项
            bg.option(ChannelOption.SO_KEEPALIVE, true);
            bg.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            bg.handler(
                    new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 打日志
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            // 添加http客户端编解码器
                            ch.pipeline().addLast(new HttpClientCodec());
                            // 分块传输
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            // http的post处理
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            // websocket连接
                            ch.pipeline()
                                    .addLast(
                                            new WebSocketClientProtocolHandler(
                                                    WebSocketClientHandshakerFactory.newHandshaker(
                                                            webSocketURL,
                                                            WebSocketVersion.V13,
                                                            null,
                                                            false,
                                                            new DefaultHttpHeaders())));
                            // protobuf可变长度解码器
                            //                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            // protobuf可变长度编码器
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            //                    ch.pipeline().addLast(new MessageToMessageEncoder<ByteBuf>() {
                            //                        @Override
                            //                        protected void encode(ChannelHandlerContext
                            // channelHandlerContext, ByteBuf object, List<Object> out) throws Exception {
                            //                            out.add(new BinaryWebSocketFrame(object));
                            //                        }
                            //                    });
                            // protobuf 解码器
                            ch.pipeline().addLast(new ProtobufDecoder(ProtoMsg.Message.getDefaultInstance()));

                            // 协议包编码
                            ch.pipeline()
                                    .addLast(
                                            new MessageToMessageEncoder<MessageLiteOrBuilder>() {
                                                @Override
                                                protected void encode(
                                                        ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out)
                                                        throws Exception {
                                                    ByteBuf result = null;
                                                    if (msg instanceof MessageLite) {
                                                        // 没有build的Protobuf消息
                                                        result = wrappedBuffer(((MessageLite) msg).toByteArray());
                                                    }
                                                    if (msg instanceof MessageLite.Builder) {
                                                        // 经过build的Protobuf消息
                                                        result =
                                                                wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
                                                    }

                                                    // 将Protbuf消息包装成Binary Frame 消息
                                                    WebSocketFrame frame = new BinaryWebSocketFrame(result);
                                                    out.add(frame);
                                                }
                                            });

                            // protobuf 编码器
                            //                    ch.pipeline().addLast(new ProtobufEncoder());

                            ch.pipeline()
                                    .addLast(
                                            new SimpleChannelInboundHandler<TextWebSocketFrame>() {

                                                @Override
                                                protected void channelRead0(
                                                        ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
                                                    System.err.println(" 客户端收到消息======== " + msg.text());
                                                }

                                                @Override
                                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
                                                        throws Exception {
                                                    if (WebSocketClientProtocolHandler.ClientHandshakeStateEvent
                                                            .HANDSHAKE_COMPLETE
                                                            .equals(evt)) {
                                                        log.info(ctx.channel().id().asShortText() + " 握手完成！");
                                                        User user = new User();
                                                        user.setId(3L);
                                                        user.setToken("9be5ccdd-edc8-43f8-aaad-36ee20f34dd2");
                                                        ProtoMsg.Message message = new LoginRequestConverter().build(user);

                                                        //                                WebSocketFrame frame = new
                                                        // BinaryWebSocketFrame(wrappedBuffer(payload));
                                                        ctx.writeAndFlush(message)
                                                                .addListener(
                                                                        new ChannelFutureListener() {
                                                                            @Override
                                                                            public void operationComplete(ChannelFuture channelFuture)
                                                                                    throws Exception {
                                                                                if (channelFuture.isDone()) {
                                                                                    log.info("成功");
                                                                                }
                                                                            }
                                                                        });
                                                    }
                                                    super.userEventTriggered(ctx, evt);
                                                }
                                            });
                        }
                    });

            ChannelFuture channelFuture =
                    bg.connect(webSocketURL.getHost(), webSocketURL.getPort()).sync();
            channelFuture.addListener(
                    new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            log.info("客户端连接成功");
                        }
                    });

            // 7 监听通道关闭事件
            // 应用程序会一直等待，直到channel关闭
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 8 优雅关闭EventLoopGroup，
            // 释放掉所有资源包括创建的线程
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
