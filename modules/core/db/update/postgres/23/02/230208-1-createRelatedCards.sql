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
);