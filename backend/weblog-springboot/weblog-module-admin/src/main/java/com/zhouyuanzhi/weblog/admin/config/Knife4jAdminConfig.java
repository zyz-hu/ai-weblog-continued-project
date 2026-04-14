package com.zhouyuanzhi.weblog.admin.config;

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
public class Knife4jAdminConfig {

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin 后台接口")
                .packagesToScan("com.zhouyuanzhi.weblog.admin.controller")
                .build();
    }

    @Bean
    public Info adminApiInfo() {
        return new Info()
                .title("Weblog 博客 Admin 后台接口文档")
                .description("Weblog 是一款由 Spring Boot + Vue 3.2 + Vite 4.3 开发的前后端分离博客。")
                .termsOfService("https://www.zhouyuanzhi.com/")
                .contact(new Contact().name("周元智").url("https://www.zhouyuanzhi.com").email("3502949828@qq.com"))
                .version("1.0");
    }
}
