-- DDL

-- 파일 컨텐츠 추가
create table binary_contents
(
    id           uuid primary key      default gen_random_uuid(),
    file_name    varchar(255) not null,
    size         integer      not null check (size >= 0),
    content_type varchar(50)  not null,
    created_at   timestamptz  not null default now()
);

CREATE TABLE users
(
    id           uuid primary key             default gen_random_uuid(),
    username     varchar(50) unique  not null,
    nickname     varchar(50)         not null,
    email        varchar(100) unique not null,
    password     varchar(200)        not null,
    phone_num    varchar(20)         not null,
    user_type    varchar(10)         not null default 'COMMON',
    phone_region varchar(10)         not null default 'KR',
    is_active    boolean             not null default true,
    introduce    text,
    profile_id   uuid,
    created_at   timestamptz         not null default now(),
    updated_at   timestamptz                  default now(),

--     외래키 설정
    constraint fk_profile foreign key (profile_id) references binary_contents (id) on delete set null
);

CREATE TABLE user_statuses
(
    id             uuid primary key     default gen_random_uuid(),
    user_id        uuid unique not null,
    last_active_at timestamptz not null default now(),
    created_at     timestamptz not null default now(),
    updated_at     timestamptz          default now(),

--     외래키 설정
    constraint fk_user foreign key (user_id) references users (id) on delete cascade
);

CREATE TABLE channels
(
    id           uuid primary key     default gen_random_uuid(),
    owner_id     uuid        not null,
    name         varchar(100)         default 'Unnamed Channel',
    description  text,
    channel_type varchar(10) not null check (channel_type in ('PUBLIC', 'PRIVATE')),
    created_at   timestamptz not null default now(),
    updated_at   timestamptz          default now(),

--     외래키 설정
    constraint fk_owner foreign key (owner_id) references users (id) on delete set null
);

CREATE TABLE messages
(
    id         uuid primary key     default gen_random_uuid(),
    channel_id uuid        not null,
    author_id  uuid        not null,
    content    text        not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz          default now(),

--     외래키 설정
    constraint fk_author foreign key (author_id) references users (id) on delete set null,
    constraint fk_channel foreign key (channel_id) references channels (id) on delete cascade
);

CREATE TABLE read_statuses
(
    id           uuid primary key     default gen_random_uuid(),
    channel_id   uuid        not null,
    user_id      uuid        not null,
    last_read_at timestamptz not null default now(),
    created_at   timestamptz not null default now(),
    updated_at   timestamptz          default now(),

--     외래키 설정
    constraint fk_user foreign key (user_id) references users (id) on delete cascade,
    constraint fk_channel foreign key (channel_id) references channels (id) on delete cascade,

-- 복합 UNIQUE 제약 조건
    constraint unique_user_channel unique (user_id, channel_id)
);

CREATE TABLE message_attachments
(
    id            uuid primary key     default gen_random_uuid(),
    message_id    uuid        not null,
    attachment_id uuid        not null,
    created_at    timestamptz not null default now(),

--     외래키 설정
    constraint fk_message foreign key (message_id) references messages (id) on delete cascade,
    constraint fk_attachment_file foreign key (attachment_id) references binary_contents (id) on delete cascade
);

CREATE TABLE channel_members
(
    id         uuid primary key     default gen_random_uuid(),
    member_id  uuid        not null,
    channel_id uuid        not null,
    created_at timestamptz not null default now(),

--     외래키 설정
    constraint fk_member foreign key (member_id) references users (id) on delete cascade,
    constraint fk_channel foreign key (channel_id) references channels (id) on delete cascade
);