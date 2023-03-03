/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package process.mpaEndorsement.activity

import com.haulmont.thesis.core.app.ThesisConstants
import com.haulmont.thesis.core.entity.PackageDoc
import com.haulmont.workflow.core.activity.ActivityHelper
import com.haulmont.workflow.core.entity.Card
import org.jbpm.api.activity.ActivityExecution
import org.jbpm.api.activity.ExternalActivityBehaviour

/**
 *
 * @author Lebedev.A
 */
class CardImprovementDecider implements ExternalActivityBehaviour {
    @Override
    void execute(ActivityExecution execution) throws Exception {
        Card card = ActivityHelper.findCard(execution);
        if (card instanceof PackageDoc || cardHasFormalizedAttachments(card)) {
            execution.take("no")
        } else {
            execution.take("yes")
        }
    }

    @Override
    void signal(ActivityExecution execution, String signalName, Map<String, ?> parameters) throws Exception {
        execution.take(signalName)
    }

    protected boolean cardHasFormalizedAttachments(Card card) {
        if (card.attachments) {
            def formalizedAttachment = card.attachments.find {
                [ThesisConstants.INCOMING_FD_ATT_TYPE_CODE, ThesisConstants.OUTGOING_FD_ATT_TYPE_CODE]
                        .contains(it.attachType?.code)
            }
            if (formalizedAttachment)
                return true;
        }

        return false
    }
}
