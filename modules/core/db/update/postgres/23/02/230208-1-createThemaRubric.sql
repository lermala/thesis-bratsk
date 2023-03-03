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
);