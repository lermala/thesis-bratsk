/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */
package com.company.bratsk.process;

import java.util.Arrays;
import java.util.List;

public class MpaEndorsementConstants {

    public static final String PROC_NAME = "MPA Endorsement";
    public static final String PROC_CODE = "mpaEndorsement";

    public static final String PROC_ROLE_INITIATOR = "Initiator";
    public static final String PROC_ROLE_PREPARED = "Prepared";
    public static final String PROC_ROLE_ENDORSEMENT = "Endorsement";
    public static final String PROC_ROLE_APPROVER = "Approver";
    public static final String PROC_ROLE_SECRETARY = "Secretary";

    public static final String STATE_NEW = "New";
    public static final String STATE_ENDORSEMENT = "Endorsement";
    public static final String STATE_IMPROVEMENT = "Improvement";
    public static final String STATE_PREPARATION = "Preparation";
    public static final String STATE_APPROVAL = "Approval";
    public static final String STATE_APPROVED = "Approved";
    public static final String STATE_NOT_APPROVED = "NotApproved";
    public static final String STATE_REGISTRATION = "Registration";

    public static final List<String> SINGLE_USER_PROC_ROLE_CODES = Arrays.asList(
            PROC_ROLE_INITIATOR,
            PROC_ROLE_PREPARED,
            PROC_ROLE_APPROVER,
            PROC_ROLE_SECRETARY
    );

}