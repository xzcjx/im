package com.xzccc.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
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

    public void run() {
        ServerBootstrap bg = new ServerBootstrap();

        try {
            boss = new NioEventLoopGroup(1);
            work = new NioEventLoopGroup();

            // 设置reactor模式的boss和woker线程
            bg.group(boss, work);

            // 设置Nio类型的Channel
            bg.channel(NioServerSocketChannel.class);

            // 绑定监听端口
            bg.localAddress(new InetSocketAddress(port));

            // 设置通道选项
            bg.option(ChannelOption.SO_KEEPALIVE, true);
            bg.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bg.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            bg.childHandler(new ChannelInitializer<SocketChannel>() {
                //有连接到达时会创建一个channel
                protected void initChannel(SocketChannel ch) throws Exception {
//                // 管理pipeline中的Handler
//                ch.pipeline().addLast(new SimpleProtobufDecoder());
//                ch.pipeline().addLast(new SimpleProtobufEncoder());
////                    ch.pipeline().addLast(new HeartBeatServerHandler());
//                // 在流水线中添加handler来处理登录,登录后删除
//                ch.pipeline().addLast("login",loginRequestHandler);
////                    ch.pipeline().addLast(chatRedirectHandler);
//                ch.pipeline().addLast(serverExceptionHandler);
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
