package com.xzccc.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
public class SwaggerConfig {
    @Value("${spring.profiles.active:dev}")
    private String active;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true)//enable是否启动Swagger,如果为false,则Swagger不能在浏览器中访问
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xzccc.server.controller"))
                //.paths(PathSelectors.ant("/lv/**"))
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("xzcc", "", "1986243742@qq.com");
        return new ApiInfo(
                "xzccc-im的swaggerAPI文档",
                "wsnid",
                "v1.0",
                "",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }


}
