-- Spring AI 2.1 Session Memory JDBC 建表脚本（MySQL）
-- 与 spring-ai-session-jdbc 2.1.0 的 schema-mysql.sql 保持一致，可重复执行。
-- 应用侧默认 spring.ai.session.repository.jdbc.initialize-schema=always；
-- 生产环境可改为 never，并手动执行本脚本。

CREATE TABLE IF NOT EXISTS AI_SESSION (
    id            VARCHAR(255)  COLLATE utf8mb4_bin NOT NULL PRIMARY KEY,
    created_at    DATETIME(6)   NOT NULL,
    expires_at    DATETIME(6),
    metadata      LONGTEXT      NOT NULL,
    event_version BIGINT        NOT NULL DEFAULT 0,
    INDEX idx_ai_session_expires_at (expires_at)
);

CREATE TABLE IF NOT EXISTS AI_SESSION_INDEX (
    session_id    VARCHAR(255)  COLLATE utf8mb4_bin NOT NULL,
    attr_name     VARCHAR(255)  COLLATE utf8mb4_bin NOT NULL,
    attr_value    VARCHAR(512)  COLLATE utf8mb4_bin NOT NULL,
    PRIMARY KEY (session_id, attr_name),
    INDEX idx_ai_session_index_name_value (attr_name, attr_value),
    CONSTRAINT fk_ai_session_index_session
        FOREIGN KEY (session_id) REFERENCES AI_SESSION (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS AI_SESSION_EVENT (
    seq             BIGINT        NOT NULL AUTO_INCREMENT,
    id              VARCHAR(255)  COLLATE utf8mb4_bin NOT NULL PRIMARY KEY,
    session_id      VARCHAR(255)  COLLATE utf8mb4_bin NOT NULL,
    timestamp       DATETIME(6)   NOT NULL,
    event_type      VARCHAR(64)   NOT NULL DEFAULT 'MESSAGE',
    message_type    VARCHAR(20),
    message_content LONGTEXT,
    payload         LONGTEXT      NOT NULL,
    synthetic       TINYINT(1)    NOT NULL DEFAULT 0,
    metadata        LONGTEXT      NOT NULL,
    UNIQUE KEY uq_ai_session_event_seq (seq),
    INDEX idx_ai_session_event_session_seq (session_id, seq),
    INDEX idx_ai_session_event_session_type_seq (session_id, event_type, seq),
    CONSTRAINT fk_ai_session_event_session
        FOREIGN KEY (session_id) REFERENCES AI_SESSION (id) ON DELETE CASCADE
);