alter table BRATSK_MPA add constraint FK_BRATSK_MPA_ON_APPROVER_USER foreign key (APPROVER_USER_ID) references SEC_USER(ID);
alter table BRATSK_MPA add constraint FK_BRATSK_MPA_ON_CARD foreign key (CARD_ID) references WF_CARD(ID) on delete CASCADE;
create index IDX_BRATSK_MPA_ON_APPROVER_USER on BRATSK_MPA (APPROVER_USER_ID);
