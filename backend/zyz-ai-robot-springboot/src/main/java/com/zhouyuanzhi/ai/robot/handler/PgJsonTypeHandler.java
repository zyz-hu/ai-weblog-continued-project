package com.zhouyuanzhi.ai.robot.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * PostgreSQL 专用 JSON 处理器
 * 解决报错: column is of type jsonb but expression is of type character varying
 */
@MappedTypes({Object.class})
public class PgJsonTypeHandler extends JacksonTypeHandler {

    public PgJsonTypeHandler(Class<?> type) {
        super(type);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        // 核心修复点：
        // MyBatis 默认用 setString 发送 JSON，PG 会报错。
        // 这里强制改用 setObject + Types.OTHER，PG 驱动就能正确识别这是 JSONB 数据了。
        ps.setObject(i, this.toJson(parameter), Types.OTHER);
    }
}