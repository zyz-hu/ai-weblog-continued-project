CREATE TABLE IF NOT EXISTS t_article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    cover VARCHAR(512) NULL,
    summary VARCHAR(1024) NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    read_num BIGINT NOT NULL DEFAULT 0,
    KEY idx_article_create_time (create_time),
    KEY idx_article_is_deleted (is_deleted)
);

CREATE TABLE IF NOT EXISTS t_article_content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL,
    content LONGTEXT NOT NULL,
    UNIQUE KEY uk_article_content_article_id (article_id)
);

CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_category_name (name),
    KEY idx_category_is_deleted (is_deleted)
);

CREATE TABLE IF NOT EXISTS t_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tag_name (name),
    KEY idx_tag_is_deleted (is_deleted)
);

CREATE TABLE IF NOT EXISTS t_article_category_rel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    UNIQUE KEY uk_article_category_rel_article_id (article_id),
    KEY idx_article_category_rel_category_id (category_id)
);

CREATE TABLE IF NOT EXISTS t_article_tag_rel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    UNIQUE KEY uk_article_tag_rel_article_tag (article_id, tag_id),
    KEY idx_article_tag_rel_article_id (article_id),
    KEY idx_article_tag_rel_tag_id (tag_id)
);

CREATE TABLE IF NOT EXISTS t_blog_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    logo VARCHAR(512) NULL,
    name VARCHAR(255) NOT NULL,
    author VARCHAR(255) NULL,
    introduction VARCHAR(1024) NULL,
    avatar VARCHAR(512) NULL,
    github_homepage VARCHAR(512) NULL,
    csdn_homepage VARCHAR(512) NULL,
    gitee_homepage VARCHAR(512) NULL,
    zhihu_homepage VARCHAR(512) NULL
);

CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(128) NOT NULL,
    password VARCHAR(255) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_username (username),
    KEY idx_user_is_deleted (is_deleted)
);

CREATE TABLE IF NOT EXISTS t_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(128) NOT NULL,
    role VARCHAR(128) NOT NULL,
    create_time DATETIME NOT NULL,
    UNIQUE KEY uk_user_role_username_role (username, role),
    KEY idx_user_role_username (username)
);

CREATE TABLE IF NOT EXISTS t_statistics_article_pv (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pv_date DATE NOT NULL,
    pv_count BIGINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_statistics_article_pv_date (pv_date)
);
