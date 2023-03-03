-- begin BRATSK_STATUS_NORMATIV
create table BRATSK_STATUS_NORMATIV (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CODE varchar(50) not null,
    NAME varchar(50) not null,
    --
    primary key (ID)
)^
-- end BRATSK_STATUS_NORMATIV
-- begin BRATSK_MPA
create table BRATSK_MPA (
    CARD_ID uuid,
    --
    APPROVER_USER_ID uuid,
    RELATION_TYPE_MPA_STATUS_ID uuid,
    COUNT_LIST integer,
    IS_PUBLIC boolean,
    DATE_PUBLIC_SITE timestamp,
    IS_PUBLIC_SITE boolean,
    THEMATIC_PUBRIC_ID uuid,
    SENT_TO_PROCURATURA boolean,
    DATE_SENT_TO_PROCURATURE date,
    PREPARED_ID uuid,
    EXECUTOR_USER_ID uuid,
    STATUS_NORMATIV_ID uuid,
    --
    primary key (CARD_ID)
)^
-- end BRATSK_MPA
-- begin TM_TASK_GROUP_TASK
alter table TM_TASK_GROUP_TASK add column RESPONSIBLE_EXECUTOR boolean ^
alter table TM_TASK_GROUP_TASK add column DTYPE varchar(31) ^
update TM_TASK_GROUP_TASK set DTYPE = 'bratsk$ExtTaskGroupTask' where DTYPE is null ^
-- end TM_TASK_GROUP_TASK
-- begin TM_TASK
alter table TM_TASK
    add column SUBTASKS_FINISHED boolean ^
-- end TM_TASK
-- begin BRATSK_RELATION_TYPE_MPA
create table BRATSK_RELATION_TYPE_MPA (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TYPE_NAME varchar(60),
    REVERSE_NAME_ID uuid,
    COMMENT_ varchar(255),
    IS_AN_ACTION boolean,
    IS_SYSTEM boolean,
    --
    primary key (ID)
)^
-- end BRATSK_RELATION_TYPE_MPA
-- begin BRATSK_RELATED_CARDS
create table BRATSK_RELATED_CARDS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SOURCE_CARD_ID uuid,
    RELATED_CARD_ID uuid,
    SOURCE_RELATION_TYPE_ID uuid,
    RELATED_RELATION_TYPE_ID uuid,
    --
    primary key (ID)
)^
-- end BRATSK_RELATED_CARDS
-- begin BRATSK_THEMA_RUBRIC
create table BRATSK_THEMA_RUBRIC (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(500),
    PARENT_THEMA_RUBRIC_ID uuid,
    --
    primary key (ID)
)^
-- end BRATSK_THEMA_RUBRIC
