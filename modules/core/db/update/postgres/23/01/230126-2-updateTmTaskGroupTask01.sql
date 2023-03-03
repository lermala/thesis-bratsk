alter table TM_TASK_GROUP_TASK add column DTYPE varchar(31) ^
update TM_TASK_GROUP_TASK set DTYPE = 'bratsk$ExtTaskGroupTask' where DTYPE is null ;