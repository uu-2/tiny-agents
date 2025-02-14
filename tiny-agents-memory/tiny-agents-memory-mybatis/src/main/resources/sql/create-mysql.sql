
CREATE TABLE IF NOT EXISTS t_chat_message (
    id int(10) AUTO_INCREMENT PRIMARY KEY,
    chat_id varchar(255) NOT NULL,
    clazz varchar(512) NOT NULL,
    message LongText NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);