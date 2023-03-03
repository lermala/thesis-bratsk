insert into BRATSK_RELATION_TYPE_MPA values ('b1813edd-7bf3-49a5-a7cf-3ddfc7635aba', 1, now(), 'admin', null, null, null, null, 'Проект', null, null, false, true)^
insert into BRATSK_RELATION_TYPE_MPA values ('677fd2d9-15ba-4d97-ba54-9085c9712044', 1, now(), 'admin', null, null, null, null, 'Действует', null, null, false, true)^
insert into BRATSK_RELATION_TYPE_MPA values ('6c9613d4-fe8d-4f1c-8336-a81913e911b6', 1, now(), 'admin', null, null, null, null, 'Вносит изменения (дополнения)', null, null, true, false)^
insert into BRATSK_RELATION_TYPE_MPA values ('c8455ba7-08c3-4b44-941f-446f3d561a14', 1, now(), 'admin', null, null, null, null, 'Действует с изменениями (дополнениями)', null, null, false, false)^
insert into BRATSK_RELATION_TYPE_MPA values ('ad1a7dd9-190b-47d9-888a-47c1fde8966d', 1, now(), 'admin', null, null, null, null, 'Признает утратившим силу', null, null, true, false)^
insert into BRATSK_RELATION_TYPE_MPA values ('244d2acd-bb42-4447-8505-b1ad66336938', 1, now(), 'admin', null, null, null, null, 'Утратил силу', null, null, false, false)^
insert into BRATSK_RELATION_TYPE_MPA values ('599c1dab-ce55-42e9-a9f7-7bd09f6bb767', 1, now(), 'admin', null, null, null, null, 'Отменяет', null, null, true, false)^
insert into BRATSK_RELATION_TYPE_MPA values ('80f35cb2-e1bb-44e1-a0d5-0391e6ea0bf7', 1, now(), 'admin', null, null, null, null, 'Отменен', null, null, false, false)^
insert into BRATSK_RELATION_TYPE_MPA values ('af160c98-cb03-4fa5-8a84-5c7ff8809bd4', 1, now(), 'admin', null, null, null, null, 'Приостанавливает действие', null, null, true, false)^
insert into BRATSK_RELATION_TYPE_MPA values ('f9502feb-fbbb-4201-b5b7-fecf739ff2ad', 1, now(), 'admin', null, null, null, null, 'Действие приостановлено', null, null, false, false)^

update BRATSK_RELATION_TYPE_MPA set REVERSE_NAME_ID = '6c9613d4-fe8d-4f1c-8336-a81913e911b6' where ID = 'c8455ba7-08c3-4b44-941f-446f3d561a14'^
update BRATSK_RELATION_TYPE_MPA set REVERSE_NAME_ID = 'c8455ba7-08c3-4b44-941f-446f3d561a14' where ID = '6c9613d4-fe8d-4f1c-8336-a81913e911b6'^
update BRATSK_RELATION_TYPE_MPA set REVERSE_NAME_ID = 'ad1a7dd9-190b-47d9-888a-47c1fde8966d' where ID = '244d2acd-bb42-4447-8505-b1ad66336938'^
update BRATSK_RELATION_TYPE_MPA set REVERSE_NAME_ID = '244d2acd-bb42-4447-8505-b1ad66336938' where ID = 'ad1a7dd9-190b-47d9-888a-47c1fde8966d'^
update BRATSK_RELATION_TYPE_MPA set REVERSE_NAME_ID = '599c1dab-ce55-42e9-a9f7-7bd09f6bb767' where ID = '80f35cb2-e1bb-44e1-a0d5-0391e6ea0bf7'^
update BRATSK_RELATION_TYPE_MPA set REVERSE_NAME_ID = '80f35cb2-e1bb-44e1-a0d5-0391e6ea0bf7' where ID = '599c1dab-ce55-42e9-a9f7-7bd09f6bb767'^
update BRATSK_RELATION_TYPE_MPA set REVERSE_NAME_ID = 'af160c98-cb03-4fa5-8a84-5c7ff8809bd4' where ID = 'f9502feb-fbbb-4201-b5b7-fecf739ff2ad'^
update BRATSK_RELATION_TYPE_MPA set REVERSE_NAME_ID = 'f9502feb-fbbb-4201-b5b7-fecf739ff2ad' where ID = 'af160c98-cb03-4fa5-8a84-5c7ff8809bd4'^