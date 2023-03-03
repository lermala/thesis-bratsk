/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package process.mpaEndorsement.listeners

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.TimeSource
import com.haulmont.thesis.core.entity.Doc
import com.haulmont.workflow.core.app.WfUtils
import com.haulmont.workflow.core.entity.Assignment
import org.jbpm.api.listener.EventListener
import org.jbpm.api.listener.EventListenerExecution

/**
 *
 * <p>$Id$</p>
 *
 * @author shishov
 */
class EndorsementEventListener implements EventListener {

    String transition
    
    @Override
    public void notify(EventListenerExecution execution) throws Exception {
        Doc doc = (Doc) com.haulmont.workflow.core.activity.ActivityHelper.findCard(execution);
        if ('start_endorsement'.equals(transition) && !doc.endorsementStartDate)
            doc.endorsementStartDate = AppBeans.get(TimeSource.class).currentTimestamp()
        else if ('end_endorsement'.equals(transition)) {
            def refuseOnly = execution.getVariable('refusedOnly')
            if (refuseOnly && Boolean.valueOf(refuseOnly)) {
                def list = doc.assignments.findAll {it.name && it.name.startsWith('Endorsement')}
                if (list) {
                    Assignment endorsementAsm = list.max {it.finished}
                    if (endorsementAsm)
                        doc.endorsementEndDate = endorsementAsm.finished
                }
            }
            else
                doc.endorsementEndDate = AppBeans.get(TimeSource.class).currentTimestamp()
        }
        else if ('approval'.equals(transition))  {
            doc.approvalDate = AppBeans.get(TimeSource.class).currentTimestamp()
            doc.endorsed = true
        }
        else if ('complete'.equals(transition))  {
            doc.endorsed = true
        }
        else if ('started'.equals(transition) || "Canceled".equals(execution.state) ||
                WfUtils.isCardInState(doc, "NotApproved")) {
            doc.endorsementStartDate = null
            doc.endorsementEndDate = null
            doc.approvalDate = null
            doc.endorsed = false
        } else if ('create_version'.equals(transition)) {
            doc.endorsementEndDate = null
        }
    }
}
