alter table BRATSK_MPA add constraint FK_BRATSK_MPA_ON_THEMATIC_PUBRIC foreign key (THEMATIC_PUBRIC_ID) references BRATSK_THEMA_RUBRIC(ID);
create index IDX_BRATSK_MPA_ON_THEMATIC_PUBRIC on BRATSK_MPA (THEMATIC_PUBRIC_ID);