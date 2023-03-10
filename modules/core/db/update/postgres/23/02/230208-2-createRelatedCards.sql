alter table BRATSK_RELATED_CARDS add constraint FK_BRATSK_RELATED_CARDS_ON_SOURCE_CARD foreign key (SOURCE_CARD_ID) references WF_CARD(ID);
alter table BRATSK_RELATED_CARDS add constraint FK_BRATSK_RELATED_CARDS_ON_RELATED_CARD foreign key (RELATED_CARD_ID) references WF_CARD(ID);
alter table BRATSK_RELATED_CARDS add constraint FK_BRATSK_RELATED_CARDS_ON_SOURCE_RELATION_TYPE foreign key (SOURCE_RELATION_TYPE_ID) references BRATSK_RELATION_TYPE_MPA(ID);
alter table BRATSK_RELATED_CARDS add constraint FK_BRATSK_RELATED_CARDS_ON_RELATED_RELATION_TYPE foreign key (RELATED_RELATION_TYPE_ID) references BRATSK_RELATION_TYPE_MPA(ID);
create index IDX_BRATSK_RELATED_CARDS_ON_SOURCE_CARD on BRATSK_RELATED_CARDS (SOURCE_CARD_ID);
create index IDX_BRATSK_RELATED_CARDS_ON_RELATED_CARD on BRATSK_RELATED_CARDS (RELATED_CARD_ID);
create index IDX_BRATSK_RELATED_CARDS_ON_SOURCE_RELATION_TYPE on BRATSK_RELATED_CARDS (SOURCE_RELATION_TYPE_ID);
create index IDX_BRATSK_RELATED_CARDS_ON_RELATED_RELATION_TYPE on BRATSK_RELATED_CARDS (RELATED_RELATION_TYPE_ID);
