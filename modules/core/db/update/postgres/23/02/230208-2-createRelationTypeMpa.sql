alter table BRATSK_RELATION_TYPE_MPA add constraint FK_BRATSK_RELATION_TYPE_MPA_ON_REVERSE_NAME foreign key (REVERSE_NAME_ID) references BRATSK_RELATION_TYPE_MPA(ID);
create unique index IDX_BRATSK_RELATION_TYPE_MPA_UK_TYPE_NAME on BRATSK_RELATION_TYPE_MPA (TYPE_NAME) where DELETE_TS is null ;
create index IDX_BRATSK_RELATION_TYPE_MPA_ON_REVERSE_NAME on BRATSK_RELATION_TYPE_MPA (REVERSE_NAME_ID);