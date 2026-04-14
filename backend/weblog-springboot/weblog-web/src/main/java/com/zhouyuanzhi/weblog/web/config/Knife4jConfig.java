package com.zhouyuanzhi.weblog.web.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author: zyz
 * @description: Knife4j 配置
 **/
@Configuration
@Profile("dev") // 只在 dev 环境中开启
public class Knife4jConfig {

    @Bean
    public GroupedOpenApi webApi() {
        return GroupedOpenApi.builder()
                .group("Web 前台接口")
                .packagesToScan("com.zhouyuanzhi.weblog.web.controller")
                .build();
    }

    @Bean
    public Info apiInfo() {
        return new Info()
                .title("Weblog 博客前台接口文档")
                .description("Weblog 是一款由 Spring Boot + Vue 3.2 + Vite 4.3 开发的前后端分离博客。")
                .termsOfService("https://www.zhouyuanzhi.com/")
                .contact(new Contact().name("周元智").url("https://www.zhouyuanzhi.com").email("871361652@qq.com"))
                .version("1.0");
    }
}
