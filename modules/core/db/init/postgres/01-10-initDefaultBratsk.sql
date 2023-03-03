/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

-- Процесс "Согласование МПА"
update WF_PROC
set NAME       = 'Согласование МПА',
    LOC_NAME   = '{"captionWithLanguageList": [{"language":"ru","caption":"Согласование МПА"},{"language":"en","caption":"MPA endorsement"}] }',
    CARD_TYPES = ',bratsk$Mpa,'
where JBPM_PROCESS_KEY = 'mpaEndorsement'
  and UPDATED_BY is null;

update WF_PROC_ROLE
set NAME       = 'Инициатор',
    LOC_NAME   = '{"captionWithLanguageList": [{"language":"ru","caption":"Инициатор"},{"language":"en","caption":"Initiator"}] }',
    ROLE_ID    = (select ID
                  from SEC_ROLE
                  where NAME = 'doc_initiator'),
    SORT_ORDER = 1
where CODE = 'Initiator'
  and PROC_ID = (select ID
                 from WF_PROC
                 where JBPM_PROCESS_KEY = 'mpaEndorsement')
  and UPDATED_BY is null;

update WF_PROC_ROLE
set NAME       = 'Подготовивший',
    LOC_NAME   = '{"captionWithLanguageList": [{"language":"ru","caption":"Подготовивший"},{"language":"en","caption":"Prepared"}] }',
    ROLE_ID    = (select ID
                  from SEC_ROLE
                  where NAME = 'doc_prepared'),
    SORT_ORDER = 2
where CODE = 'Prepared'
  and PROC_ID = (select ID
                 from WF_PROC
                 where JBPM_PROCESS_KEY = 'mpaEndorsement')
  and UPDATED_BY is null;

update WF_PROC_ROLE
set NAME       = 'Согласующий',
    LOC_NAME   = '{"captionWithLanguageList": [{"language":"ru","caption":"Согласующий"},{"language":"en","caption":"Endorsement"}] }',
    ROLE_ID    = (select ID
                  from SEC_ROLE
                  where NAME = 'doc_endorsement'),
    SORT_ORDER = 3
where CODE = 'Endorsement'
  and PROC_ID = (select ID
                 from WF_PROC
                 where JBPM_PROCESS_KEY = 'mpaEndorsement')
  and UPDATED_BY is null;

update WF_PROC_ROLE
set NAME       = 'Утверждающий',
    LOC_NAME   = '{"captionWithLanguageList": [{"language":"ru","caption":"Утверждающий"},{"language":"en","caption":"Approver"}] }',
    ROLE_ID    = (select ID
                  from SEC_ROLE
                  where NAME = 'doc_approver'),
    SORT_ORDER = 4
where CODE = 'Approver'
  and PROC_ID = (select ID
                 from WF_PROC
                 where JBPM_PROCESS_KEY = 'mpaEndorsement')
  and UPDATED_BY is null;

update WF_PROC_ROLE
set NAME       = 'Секретарь',
    LOC_NAME   = '{"captionWithLanguageList": [{"language":"ru","caption":"Секретарь"},{"language":"en","caption":"Secretary"}] }',
    ROLE_ID    = (select ID
                  from SEC_ROLE
                  where NAME = 'doc_secretary'),
    SORT_ORDER = 5
where CODE = 'Secretary'
  and PROC_ID = (select ID
                 from WF_PROC
                 where JBPM_PROCESS_KEY = 'mpaEndorsement')
  and UPDATED_BY is null;