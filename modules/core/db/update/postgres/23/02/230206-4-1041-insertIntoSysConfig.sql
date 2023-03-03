--Insert data into SYS_CONFIG
insert into SYS_CONFIG (ID,
                        CREATE_TS,
                        CREATED_BY,
                        VERSION,
                        UPDATE_TS,
                        NAME,
                        VALUE_)
values (newid(),
        now(),
        'admin',
        1,
        now(),
        'thesis.checkNotCompletedSubTasks',
        'false') ^
