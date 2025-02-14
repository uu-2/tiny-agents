CREATE TABLE IF NOT EXISTS t_chat_message (
    id bigserial NOT NULL PRIMARY KEY,
    chat_id varchar(255) NOT NULL,
    clazz varchar(512) NOT NULL,
    message Text NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);