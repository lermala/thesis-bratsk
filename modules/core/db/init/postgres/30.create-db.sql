-- begin insert secPermissions for StatusNormativ
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$StatusNormativ:create', 0, (select ID from SEC_ROLE where NAME = 'SimpleUser'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$StatusNormativ:delete', 0, (select ID from SEC_ROLE where NAME = 'SimpleUser'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$StatusNormativ:create', 1, (select ID from SEC_ROLE where NAME = 'Administrators'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$StatusNormativ:update', 1, (select ID from SEC_ROLE where NAME = 'Administrators'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$StatusNormativ:delete', 1, (select ID from SEC_ROLE where NAME = 'Administrators'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$StatusNormativ:update', 0, (select ID from SEC_ROLE where NAME = 'SimpleUser'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$StatusNormativ:create', 1, (select ID from SEC_ROLE where NAME = 'ReferenceEditor'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$StatusNormativ:update', 1, (select ID from SEC_ROLE where NAME = 'ReferenceEditor'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$StatusNormativ:delete', 1, (select ID from SEC_ROLE where NAME = 'ReferenceEditor'))^
-- end insert secPermissions for StatusNormativ
-- begin insert default numerator for Mpa
CREATE OR REPLACE FUNCTION baseInsert()
RETURNS integer
AS $$
DECLARE
    cnt integer = 0;
BEGIN
cnt = (select count(id) from DF_NUMERATOR where CODE = 'MpaNumerator' and delete_ts is null);
if(cnt = 0) then
    INSERT INTO DF_NUMERATOR (ID, CREATE_TS, CREATED_BY, VERSION, CODE, NUMERATOR_FORMAT, SCRIPT_ENABLED,
                              PERIODICITY, NUMBER_INITIAL_VALUE, LOC_NAME)
    VALUES ('e0945f2d-e218-44ca-829e-b31cb6a67a45', now(), 'system', 1, 'MpaNumerator', '[number]', FALSE, 'Y', 1,
            '{"captionWithLanguageList":[{"language":"ru","caption":"МПА"},{"language":"en","caption":"MPA"}]}'
    );
end if;

return 0;
END;
$$
LANGUAGE plpgsql;
^

select baseInsert()^
drop function if exists baseInsert()^
-- end insert default numerator for Mpa
-- begin insert cardType for Mpa
insert into TS_CARD_TYPE (ID, CREATE_TS, CREATED_BY, NAME, DISCRIMINATOR, FIELDS_XML)
       values ('be882053-9c49-4cb2-9524-86c03c69480f', now(), 'system', 'bratsk$Mpa', '2000', '<?xml version="1.0" encoding="UTF-8"?>
<fields>
  <field name="date" inDocKind="true" required="false" visible="true" signed="false" />
  <field name="docCategory" inDocKind="true" required="false" visible="false" signed="false" />
  <field name="owner" inDocKind="true" required="false" visible="true" signed="false" />
  <field name="department" inDocKind="true" required="false" visible="true" signed="false" />
  <field name="comment" inDocKind="true" required="false" visible="true" signed="false" />
  <field name="finishDatePlan" inDocKind="false" required="false" visible="false" signed="false" />
  <field name="resolution" inDocKind="false" required="false" visible="false" signed="false" />
  <field name="number" inDocKind="true" required="false" visible="true" signed="false" />
  <field name="organization" inDocKind="true" required="false" visible="true" signed="false" />
  <field name="parentCard" inDocKind="true" required="false" visible="false" signed="false" />
  <field name="theme" inDocKind="true" required="false" visible="false" signed="false" />
  <field name="addressees" inDocKind="false" required="false" visible="false" signed="false" />
  <field name="officeExecutor" inDocKind="false" required="false" visible="false" signed="false" />
  <field name="employeeExecutor" inDocKind="false" required="false" visible="false" signed="false" />
  <field name="officeSignedBy" inDocKind="false" required="false" visible="false" signed="false" />
  <field name="receivingMethod" inDocKind="false" required="false" visible="false" signed="false" />
  <field name="responseToDoc" inDocKind="false" required="false" visible="false" signed="false" />
  <field name="sender" inDocKind="false" required="false" visible="false" signed="false" />
  <field name="postTrackingNumber" inDocKind="true" required="false" visible="true" signed="false" />
  <field name="approverUser" inDocKind="true" required="false" visible="true" signed="false" />
</fields>
')^
-- end insert cardType for Mpa
-- begin update procCardType for Mpa
update wf_proc set card_types = regexp_replace(card_types, ',bratsk\\$Mpa', '') where code in ('Endorsement', 'Resolution', 'Acquaintance', 'Registration')^
update wf_proc set updated_by='system', card_types = card_types || 'bratsk$Mpa,' where code in ('Endorsement', 'Resolution', 'Acquaintance', 'Registration')^
-- end update procCardType for Mpa
-- begin insert docKind for Mpa
CREATE OR REPLACE FUNCTION baseInsert()
RETURNS integer
AS $$
DECLARE
    cnt integer = 0;
BEGIN
    cnt = (select count(CATEGORY_ID) from DF_DOC_KIND where CATEGORY_ID = '8283f896-3251-4af0-be01-1b3a7b97cef1');
    if (cnt = 0) then
        insert into SYS_CATEGORY (ID, NAME, ENTITY_TYPE, IS_DEFAULT, CREATE_TS, CREATED_BY, VERSION, DISCRIMINATOR)
        values ('8283f896-3251-4af0-be01-1b3a7b97cef1', 'МПА', 'bratsk$Mpa', false, now(), 'system', 1, 1);

        insert into DF_DOC_KIND (CATEGORY_ID, CREATE_TS, CREATED_BY, VERSION, DOC_TYPE_ID, NUMERATOR_ID,
                                 NUMERATOR_TYPE, CATEGORY_ATTRS_PLACE, TAB_NAME, PORTAL_PUBLISH_ALLOWED, DISABLE_ADD_PROCESS_ACTORS, CREATE_ONLY_BY_TEMPLATE)
        values ('8283f896-3251-4af0-be01-1b3a7b97cef1', now(), 'system', 1, 'be882053-9c49-4cb2-9524-86c03c69480f', 'e0945f2d-e218-44ca-829e-b31cb6a67a45',
                1, 1, 'Доп. поля', false, false, false);
end if;
return 0;
END;
$$
LANGUAGE plpgsql;
^
select baseInsert();^
drop function if exists baseInsert();^
-- end insert docKind for Mpa
-- begin insert secPermissions for Mpa
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$Mpa:create', 0, (select ID from SEC_ROLE where NAME = 'SimpleUser'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$Mpa:delete', 0, (select ID from SEC_ROLE where NAME = 'SimpleUser'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$Mpa:create', 1, (select ID from SEC_ROLE where NAME = 'Administrators'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$Mpa:update', 1, (select ID from SEC_ROLE where NAME = 'Administrators'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$Mpa:delete', 1, (select ID from SEC_ROLE where NAME = 'Administrators'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$Mpa:create', 1, (select ID from SEC_ROLE where NAME = 'doc_initiator'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$Mpa:update', 1, (select ID from SEC_ROLE where NAME = 'doc_initiator'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$Mpa:delete', 1, (select ID from SEC_ROLE where NAME = 'doc_initiator'))^
-- end insert secPermissions for Mpa
-- begin update discriminator for ExtTask
update WF_CARD
set CARD_TYPE = '200'
where CARD_TYPE = '20' ^
-- end update discriminator for ExtTask
--Insert data into SYS_CONFIG
insert into SYS_CONFIG (ID,
                        CREATE_TS,
                        CREATED_BY,
                        VERSION,
                        UPDATE_TS,
                        name,
                        VALUE_)
values (newid(),
        now(),
        'admin',
        1,
        now(),
        'thesis.checkNotCompletedSubTasks',
        'false') ^


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

--Insert data for entity sec$Role
insert into SEC_ROLE (ID,
                      NAME,
                      LOC_NAME,
                      DESCRIPTION,
                      DTYPE)
values (newid(),
    'doc_prepared',
    'Подготовивший',
    'Роль дает возможность подготовки документов/договоров',
    '10');
-- begin insert secPermissions for ThemaRubric
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$ThemaRubric:create', 0, (select ID from SEC_ROLE where NAME = 'SimpleUser'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$ThemaRubric:delete', 0, (select ID from SEC_ROLE where NAME = 'SimpleUser'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$ThemaRubric:create', 1, (select ID from SEC_ROLE where NAME = 'Administrators'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$ThemaRubric:update', 1, (select ID from SEC_ROLE where NAME = 'Administrators'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$ThemaRubric:delete', 1, (select ID from SEC_ROLE where NAME = 'Administrators'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$ThemaRubric:update', 0, (select ID from SEC_ROLE where NAME = 'SimpleUser'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$ThemaRubric:create', 1, (select ID from SEC_ROLE where NAME = 'ReferenceEditor'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$ThemaRubric:update', 1, (select ID from SEC_ROLE where NAME = 'ReferenceEditor'))^
insert into SEC_PERMISSION (ID, CREATE_TS, CREATED_BY, VERSION, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values (newid(), now(), 'system', 1, now(), null, null, null, 20, 'bratsk$ThemaRubric:delete', 1, (select ID from SEC_ROLE where NAME = 'ReferenceEditor'))^
-- end insert secPermissions for ThemaRubric
