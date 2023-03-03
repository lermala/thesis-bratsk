/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.entity;

import java.util.UUID;

public interface BratskConstants {

    interface StatusNameConstants {
        UUID ACTION_STOPPED = UUID.fromString("f9502feb-fbbb-4201-b5b7-fecf739ff2ad");
        UUID EXPIRED = UUID.fromString("244d2acd-bb42-4447-8505-b1ad66336938");
        UUID PROJECT_ID = UUID.fromString("b1813edd-7bf3-49a5-a7cf-3ddfc7635aba");
        UUID CANCELED = UUID.fromString("80f35cb2-e1bb-44e1-a0d5-0391e6ea0bf7");
        UUID ACTIVE_WITH_MODIFICATIONS = UUID.fromString("c8455ba7-08c3-4b44-941f-446f3d561a14");
        UUID ACTIVE_ID = UUID.fromString("677fd2d9-15ba-4d97-ba54-9085c9712044");

    }

    interface ActionNameConstants {
        UUID CANCELS = UUID.fromString("599c1dab-ce55-42e9-a9f7-7bd09f6bb767");
        UUID ADD_MODIFICATIONS = UUID.fromString("6c9613d4-fe8d-4f1c-8336-a81913e911b6");
        UUID STOPS_ACTION = UUID.fromString("af160c98-cb03-4fa5-8a84-5c7ff8809bd4");
        UUID RECOGNIZES_AS_INVALID = UUID.fromString("ad1a7dd9-190b-47d9-888a-47c1fde8966d");
    }
}
