package com.xzccc.netty_client.starter;

import com.xzccc.netty_client.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;

@Configuration
// 自动加载配置信息

// 使包路径下带有@Value的注解自动注入
// 使包路径下带有@Autowired的类可以自动注入
@ComponentScan("com.xzccc.netty_client")
@SpringBootApplication
public class ClientApplication {

    /**
     * @param args
     */
    public static void main(String[] args) throws URISyntaxException {

        // 启动并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context = SpringApplication.run(ClientApplication.class, args);

        ChatClient chatClient = context.getBean(ChatClient.class);
        chatClient.run();
    }
}
