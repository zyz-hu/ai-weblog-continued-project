CREATE TABLE IF NOT EXISTS t_chat (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(128) NOT NULL,
    user_id VARCHAR(128) NOT NULL,
    title VARCHAR(255) NOT NULL,
    params JSONB NULL,
    is_deleted INTEGER NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    CONSTRAINT uk_t_chat_uuid UNIQUE (uuid)
);

CREATE INDEX IF NOT EXISTS idx_t_chat_user_id_update_time
    ON t_chat (user_id, update_time DESC);

CREATE INDEX IF NOT EXISTS idx_t_chat_is_deleted
    ON t_chat (is_deleted);

CREATE TABLE IF NOT EXISTS t_chat_message (
    id BIGSERIAL PRIMARY KEY,
    chat_uuid VARCHAR(128) NOT NULL,
    role VARCHAR(32) NOT NULL,
    content TEXT NOT NULL,
    reasoning_content TEXT NULL,
    model_name VARCHAR(128) NULL,
    token_usage JSONB NULL,
    meta_data JSONB NULL,
    create_time TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_t_chat_message_chat_uuid_create_time
    ON t_chat_message (chat_uuid, create_time DESC);

CREATE TABLE IF NOT EXISTS t_ai_customer_service_md_storage (
    id BIGSERIAL PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    new_file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(1024) NOT NULL,
    file_size BIGINT NOT NULL,
    status INTEGER NOT NULL,
    remark VARCHAR(1024) NULL,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_t_ai_customer_service_md_storage_status
    ON t_ai_customer_service_md_storage (status);

CREATE INDEX IF NOT EXISTS idx_t_ai_customer_service_md_storage_create_time
    ON t_ai_customer_service_md_storage (create_time DESC);

-- Additional note:
-- The AI module also uses a pgvector-backed table named t_vector_store.
-- Its exact DDL may vary depending on your Spring AI / pgvector version.
-- If you do not rely on framework auto-initialization, create that table
-- according to the Spring AI pgvector schema for your version, and keep the
-- table name aligned with the Nacos config:
-- spring.ai.vectorstore.pgvector.table-name=t_vector_store
