/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package process.mpaEndorsement.activity

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.thesis.core.app.PackageDocService
import com.haulmont.thesis.core.entity.Doc
import com.haulmont.thesis.core.entity.PackageDoc
import com.haulmont.workflow.core.activity.ActivityHelper
import com.haulmont.workflow.core.activity.IsRoleAssignedDecider
import org.apache.commons.lang.BooleanUtils
import org.jbpm.api.activity.ActivityExecution

/**
 *
 * @author shatokhin
 * @version $Id$
 */
public class RegistrationDecider extends IsRoleAssignedDecider {
    @Override
    void execute(ActivityExecution execution) throws Exception {
        Doc doc = ActivityHelper.findCard(execution);
        if (isDocRegistered(doc)) {
            execution.take("no")
        } else {
            super.execute(execution)
        }
    }

    boolean isDocRegistered(Doc doc) {
        if (doc instanceof PackageDoc) {
            return (AppBeans.get(PackageDocService.class).getUnregisteredDocCountInPackageDoc(doc.getId()) == 0)
        }
        return BooleanUtils.isTrue(doc.registered)
    }

}
