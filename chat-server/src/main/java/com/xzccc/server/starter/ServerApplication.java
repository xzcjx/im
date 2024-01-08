package com.xzccc.server.starter;


import com.xzccc.server.server.ChatServer;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//自动加载配置信息
@Configuration
//使包路径下带有@Value的注解自动注入
//使包路径下带有@Autowired的类可以自动注入
@ComponentScan(basePackages = "com.xzccc.server")
@MapperScan(basePackages = "com.xzccc.server.mapper")
@SpringBootApplication
@Slf4j
//@EnableSwagger2
//@EnableWebMvc
public class ServerApplication {


    /**
     * @param args
     */
    public static void main(String[] args) {
        // 启动并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context =
                SpringApplication.run(ServerApplication.class, args);

        //启动聊天服务器
        startChatServer(context);
    }

    //启动 重复回显的服务器


    //启动聊天服务器
    private static void startChatServer(ApplicationContext context) {
        ChatServer nettyServer = context.getBean(ChatServer.class);
        nettyServer.run();
    }


}

