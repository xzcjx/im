package com.xzccc.netty_server.server;

import com.xzccc.netty.model.msg.ProtoMsg;
import com.xzccc.netty_server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Service("ChatServer")
@Slf4j
public class ChatServer {

    @Value("${server.websocket-port}")
    private int port;
    private EventLoopGroup boss;
    private NioEventLoopGroup work;

    @Autowired
    LoginRequestHandler loginRequestHandler;

    @Autowired
    ServerExceptionHandler serverExceptionHandler;


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
                    // 支持WebSocket数据压缩
                    ch.pipeline().addLast(new WebSocketServerCompressionHandler());
                    // WebSocket协议配置，设置访问路径，WebSocket 握手、控制帧处理
                    ch.pipeline().addLast(new WebSocketServerProtocolHandler("/im", null, true));
                    // 解码器，通过Google Protocol Buffers序列化框架动态的切割接收到的ByteBuf
//                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
//                    // Protocol Buffers 长度属性编码器
//                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    // Protocol Buffer解码器
                    ch.pipeline().addLast(new ProtobufDecoder(ProtoMsg.Message.getDefaultInstance()));
                    // Protocol Buffer编码器
                    ch.pipeline().addLast(new ProtobufEncoder());
//                    // WebSocket心跳检测
////                    ch.pipeline().addLast(new HeartBeatServerHandler());
                    // 在流水线中添加handler来处理登录,登录后删除
                    ch.pipeline().addLast("login",loginRequestHandler);
////                    ch.pipeline().addLast(chatRedirectHandler);
//                    // 删除Session
                    ch.pipeline().addLast(serverExceptionHandler);
                }
            });
            ChannelFuture channelFuture = bg.bind().sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.info("服务端连接成功");
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
