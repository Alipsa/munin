create table IF NOT EXISTS users(
    username varchar(50) not null primary key,
    password varchar(60) not null,
    email varchar(50),
    enabled boolean not null default false,
    failed_attempts int not null default 0
);

create table IF NOT EXISTS authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    primary key (username,authority),
    constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index IF NOT EXISTS ix_auth_username on authorities (username,authority);
create index IF NOT EXISTS ix_username on authorities (username);

create table if not exists report (
    report_name varchar(50) not null primary key,
    description varchar(500),
    definition text,
    input_content text,
    report_type varchar(50),
    report_group varchar(50)
);

CREATE INDEX idx_report_group ON report(report_group);

create table if not exists report_schedule (
    id bigint not null primary key,
    report_name varchar(50),
    cron varchar(50),
    emails varchar(500)
);

create sequence schedule_seq START WITH 1 INCREMENT BY 1;