create table IF NOT EXISTS users(
    username varchar(50) not null primary key,
    password varchar(60) not null,
    enabled boolean not null default false,
    failed_attempts int null
);

create table IF NOT EXISTS authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    primary key (username,authority),
    constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index IF NOT EXISTS ix_auth_username on authorities (username,authority);
create index IF NOT EXISTS ix_username on authorities (username);