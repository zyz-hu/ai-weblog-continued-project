package com.zhouyuanzhi.ai.robot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ZhouyuanzhiAiRobotSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhouyuanzhiAiRobotSpringbootApplication.class, args);
    }

}
