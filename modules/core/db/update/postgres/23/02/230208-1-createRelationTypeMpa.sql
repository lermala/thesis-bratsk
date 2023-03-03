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
);