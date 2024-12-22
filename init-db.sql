create table image
(
    id         bigint auto_increment
        primary key,
    image_url  varchar(255) null,
    created_at timestamp    null,
    created_by varchar(255) null
)
    charset = utf8mb3;

create table system_code
(
    id         bigint auto_increment
        primary key,
    code       varchar(50)  null,
    created_at timestamp    null,
    created_by varchar(255) null
)
    charset = utf8mb3;

create table system_code_detail
(
    id             bigint auto_increment
        primary key,
    system_code_id bigint       null,
    code_name      varchar(500) null,
    description    varchar(255) null,
    created_at     timestamp    null,
    created_by     varchar(255) null,
    modified_at    timestamp    null,
    modified_by    varchar(255) null
)
    charset = utf8mb3;

create index system_code_detail_system_code_fk
    on system_code_detail (system_code_id);

create table issue
(
    id               bigint auto_increment
        primary key,
    content          text                 null,
    image_id         bigint               null,
    type_id          bigint               null,
    customer_id      bigint               null,
    penpot_comment_id varchar(255)        null,
    penpot_prototype_link text            null,
    due_date         date                 null,
    status           varchar(20)          null,
    version          varchar(50)          null,
    is_appendix      tinyint(1) default 0 not null,
    is_deal_customer tinyint(1) default 0 not null,
    created_at       timestamp            null,
    created_by       varchar(255)         null
)
    charset = utf8mb3;

create index issue_customer_index
    on issue (customer_id);

create index issue_image_fk
    on issue (image_id);

create index issue_type_fk
    on issue (type_id);

create table issue_history
(
    id          bigint auto_increment
        primary key,
    issue_id    bigint       null,
    content     json         null,
    modified_at timestamp    null,
    modified_by varchar(255) null
)
    charset = utf8mb3;

create index issue_history_issue_fk
    on issue_history (issue_id);

create table comment
(
    id          bigint auto_increment
        primary key,
    post_id     bigint               null,
    content     text                 null,
    vote        bigint     default 0 not null,
    is_solution tinyint(1) default 0 not null,
    created_at  timestamp            null,
    created_by  varchar(255)         null,
    modified_at timestamp            null,
    modified_by varchar(255)         null
)
    charset = utf8mb3;

create index comment_post_fk
    on comment (post_id);

create table post
(
    id          bigint auto_increment
        primary key,
    title       varchar(255) null,
    description text         null,
    issue_id    bigint       null,
    module_id   bigint       null,
    apply_for   varchar(50)  null,
    approved_at timestamp    null,
    status      varchar(20)  null,
    created_at  timestamp    null,
    created_by  varchar(255) null,
    modified_at timestamp    null,
    modified_by varchar(255) null
)
    charset = utf8mb3;

create index post_issue_fk
    on post (issue_id);

create index post_module_fk
    on post (module_id);

create table user
(
    id          bigint auto_increment
        primary key,
    username    varchar(255) null,
    password    varchar(255) null,
    name        varchar(255) null,
    type        varchar(5)   null,
    status      varchar(10)  null,
    created_at  timestamp    null,
    created_by  varchar(255) null,
    modified_at timestamp    null,
    modified_by varchar(255) null
)
    charset = utf8mb3;

create table assignee_issue
(
    user_id  bigint not null,
    issue_id bigint not null,
    primary key (user_id, issue_id)
)
    charset = utf8mb3;

create index assignee_issue_issue_fk
    on assignee_issue (issue_id);

create index assignee_issue_user_fk
    on assignee_issue (user_id);

