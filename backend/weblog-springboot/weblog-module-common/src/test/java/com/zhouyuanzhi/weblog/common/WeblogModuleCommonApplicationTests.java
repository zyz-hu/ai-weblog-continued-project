package com.zhouyuanzhi.weblog.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = WeblogModuleCommonApplicationTests.Application.class)
@Slf4j
class WeblogModuleCommonApplicationTests {

    public static class Application {
    }

    @Test
    public void test() {
    }

}
