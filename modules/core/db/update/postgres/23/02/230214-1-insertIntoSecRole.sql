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