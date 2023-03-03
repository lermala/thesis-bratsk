alter table BRATSK_MPA add constraint FK_BRATSK_MPA_ON_PREPARED foreign key (PREPARED_ID) references SEC_USER(ID);
create index IDX_BRATSK_MPA_ON_PREPARED on BRATSK_MPA (PREPARED_ID);
