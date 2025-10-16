CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamptz  NOT NULL,
    file_name    varchar(255) NOT NULL,
    size         bigint       NOT NULL,
    content_type varchar(100) NOT NULL,
    bytes        bytea        NOT NULL
);

CREATE TABLE users
(
    id         uuid primary key,
    created_at timestamptz         NOT NULL,
    updated_at timestamptz,
    username   varchar(50)         NOT NULL,
    email      varchar(100) UNIQUE NOT NULL,
    password   varchar(60)         NOT NULL,
    profile_id uuid,
    CONSTRAINT fk_user_binary_content FOREIGN KEY (profile_id) REFERENCES binary_contents (id)
        ON DELETE SET NULL
);

CREATE TABLE user_statuses
(
    id             uuid PRIMARY KEY,
    created_at     timestamptz NOT NULL,
    updated_at     timestamptz,
    user_id        uuid UNIQUE NOT NULL,
    last_active_at timestamptz NOT NULL,
    CONSTRAINT fk_user_status_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE TABLE channels
(
    id          uuid PRIMARY KEY,
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    name        varchar(100),
    description varchar(500),
    type        varchar(10) NOT NULL,
    CONSTRAINT check_channel_type CHECK ( type IN ('PUBLIC', 'PRIVATE') )
);

CREATE TABLE read_statuses
(
    id           uuid PRIMARY KEY,
    created_at   timestamptz NOT NULL,
    updated_at   timestamptz,
    user_id      uuid,
    channel_id   uuid,
    last_read_at timestamptz NOT NULL,
    CONSTRAINT fk_read_statuses_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_read_statuses_channel FOREIGN KEY (channel_id) REFERENCES channels (id),
    CONSTRAINT uk_user_channel UNIQUE (user_id, channel_id)
);

CREATE TABLE messages
(
    id         uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content    text,
    channel_id uuid        NOT NULL,
    author_id  uuid,
    CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id) REFERENCES channels (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_messages_user FOREIGN KEY (author_id) REFERENCES users (id)
        ON DELETE SET NULL
);

CREATE TABLE message_attachments
(
    message_id    uuid,
    attachment_id uuid,
    CONSTRAINT pk_message_attachments PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_message_attachments_message FOREIGN KEY (message_id) REFERENCES messages (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_message_attachment_binary_content FOREIGN KEY (attachment_id) REFERENCES binary_contents (id)
        ON DELETE CASCADE
);

SHOW search_path;

SELECT * FROM information_schema.tables
WHERE table_schema = 'public';

select count(*) from users;