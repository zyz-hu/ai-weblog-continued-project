package com.zhouyuanzhi.weblog.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author: 周元智
 * @url: www.zhouyuanzhi.com
 * @date: 2025-08-22 17:01
 * @description: Mybatis Plus 自动填充处理器
 **/
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        // 检查并设置 createTime, updateTime, isDeleted
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // isDeleted 字段，如果为 null，则设置为 false
        if (metaObject.getValue("isDeleted") == null) {
            this.setFieldValByName("isDeleted", false, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}