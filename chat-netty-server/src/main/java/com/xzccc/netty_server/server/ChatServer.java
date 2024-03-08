package com.xzccc.netty_server.server;

import static io.netty.buffer.Unpooled.wrappedBuffer;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.xzccc.netty.model.msg.ProtoMsg;
import com.xzccc.netty_server.handler.*;
import com.xzccc.netty_server.session.ServerSession;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.netty4.NettyAllocatorMetrics;
import io.micrometer.core.instrument.binder.netty4.NettyEventExecutorMetrics;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("ChatServer")
@Slf4j
public class ChatServer {

    @Autowired
    HttpLoginHandler httpLoginHandler;
    @Autowired
    ServerExceptionHandler serverExceptionHandler;
    @Autowired
    MeterRegistry meterRegistry;
    @Value("${server.websocket-port}")
    private int port;
    private EventLoopGroup boss;
    private NioEventLoopGroup work;

    public void run() {
        ServerBootstrap bg = new ServerBootstrap();

        try {
            boss = new NioEventLoopGroup(1);
            work = new NioEventLoopGroup();

            // 设置reactor模式的boss和woker线程
            bg.group(boss, work);

            // 设置Nio类型的Channel
            bg.channel(NioServerSocketChannel.class);
//            bg.handler(new LoggingHandler(LogLevel.DEBUG));

            // 绑定监听端口
            bg.localAddress(new InetSocketAddress(port));

            // 设置通道选项
            bg.option(ChannelOption.SO_KEEPALIVE, true);
            bg.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bg.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            bg.childHandler(new ChannelInitializer<SocketChannel>() {
                //有连接到达时会创建一个channel
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 管理pipeline中的Handler
                    // 日志
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    // HTTP 协议解析，用于握手阶段
                    ch.pipeline().addLast(new HttpServerCodec());
                    // 支持大数据流写入
                    ch.pipeline().addLast(new ChunkedWriteHandler());
                    // 支持参数对象解析，比如Post参数，设置聚合内容的最大长度
                    ch.pipeline().addLast(new HttpObjectAggregator(65536));
                    // 在websocket握手前鉴权
                    ch.pipeline().addLast("login",httpLoginHandler);
                    // 支持WebSocket数据压缩
//                    ch.pipeline().addLast(new WebSocketServerCompressionHandler());
                    // WebSocket协议配置，设置访问路径，由于是握手前鉴权，所以必须保证checkStartWith为true
                    ch.pipeline().addLast(new WebSocketServerProtocolHandler("/im", true));

                    // Protocol Buffers 长度属性编码器
                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    // 解码器，通过Google Protocol Buffers序列化框架动态的切割接收到的ByteBuf
                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());

                    ch.pipeline().addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                        @Override
                        protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                            log.info("received client msg ------------------------");
                            if (frame instanceof TextWebSocketFrame) {
                                // 文本消息
                                TextWebSocketFrame textFrame = (TextWebSocketFrame)frame;
                                log.info("MsgType is TextWebSocketFrame");
                                ctx.writeAndFlush(new TextWebSocketFrame("不接受文本格式数据，连接断开"));
                                ServerSession.closeSession(ctx);
                            } else if (frame instanceof BinaryWebSocketFrame) {
                                // 二进制消息
                                ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
                                objs.add(buf);
                                // 自旋累加
                                buf.retain();
                                log.info("MsgType is BinaryWebSocketFrame");
                            } else if (frame instanceof PongWebSocketFrame) {
                                // PING存活检测消息
                                log.info("MsgType is PongWebSocketFrame ");
                            } else if (frame instanceof CloseWebSocketFrame) {
                                // 关闭指令消息
                                log.info("MsgType is CloseWebSocketFrame");
                                ch.close();
                            }

                        }
                    });
                    // Protocol Buffer解码器
                    ch.pipeline().addLast(new ProtobufDecoder(ProtoMsg.Message.getDefaultInstance()));

                    // Protocol Buffer编码器
                    ch.pipeline().addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
                        @Override
                        protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                            ByteBuf result = null;
                            if (msg instanceof MessageLite) {
                                // 没有build的Protobuf消息
                                result = wrappedBuffer(((MessageLite) msg).toByteArray());
                            }
                            if (msg instanceof MessageLite.Builder) {
                                // 经过build的Protobuf消息
                                result = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
                            }

                            // 将Protbuf消息包装成Binary Frame 消息
                            WebSocketFrame frame = new BinaryWebSocketFrame(result);
                            out.add(frame);
                        }
                    });
                    // Protocol Buffer编码器
//                    ch.pipeline().addLast(new ProtobufEncoder());
//                    // WebSocket心跳检测
////                    ch.pipeline().addLast(new HeartBeatServerHandler());
////                    ch.pipeline().addLast(chatRedirectHandler);
//                    // 删除Session
                    ch.pipeline().addLast("serverExceptionHandler",serverExceptionHandler);
                }
            });
            ChannelFuture channelFuture = bg.bind().sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.info("服务端连接成功");
                    NettyAllocatorMetrics nettyAllocatorMetrics;
                    nettyAllocatorMetrics = new NettyAllocatorMetrics(PooledByteBufAllocator.DEFAULT);
                    nettyAllocatorMetrics.bindTo(meterRegistry);
                    NettyEventExecutorMetrics nettyEventExecutorMetrics = new NettyEventExecutorMetrics(work);
                    nettyEventExecutorMetrics.bindTo(meterRegistry);
//                    System.out.println(meterRegistry.getMeters());
//                    List<Meter> meters = meterRegistry.getMeters();
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
