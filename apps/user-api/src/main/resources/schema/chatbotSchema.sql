CREATE TABLE spring_ai_chat_memory (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       conversation_id VARCHAR(64) NOT NULL,
                                       content LONGTEXT NOT NULL,
                                       type VARCHAR(10) NOT NULL,
                                       timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

                                       CONSTRAINT chk_type
                                           CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL'))
);

CREATE INDEX idx_conversation_created_at
    ON spring_ai_chat_memory(conversation_id, timestamp DESC);