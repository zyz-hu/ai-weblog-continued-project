# AGENTS

## Repository Hygiene

- 不允许写完代码后主动编译与编写单元测试，除非用户明确要求。
- 开源前必须移除所有敏感信息，不要在仓库中提交真实密码、密钥、令牌、服务器地址或私有文档。
- 需要保留配置结构时，统一使用环境变量、GitHub Secrets、本地 `.env` 文件或示例占位符。
- 私有资料一律放在 Git 忽略目录中，例如 `.resume-private/`、面试材料或本地学习笔记。

## Java Convention

- 以后每个新创建的 Java 类，必须包含以下注释：

  ```java
  /**
   * @author: zyz
   * @description: 文件模块说明
   * @since ${当前本地时间}
   */
  ```
