/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */
package process.mpaEndorsement.timers;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.EntityLoadInfo;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.workflow.core.WfHelper;
import com.haulmont.workflow.core.entity.Assignment;
import com.haulmont.workflow.core.entity.CardInfo;
import com.haulmont.workflow.core.timer.AssignmentTimersFactory;
import org.jbpm.api.activity.ActivityExecution;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndorsementTimersFactory implements AssignmentTimersFactory {

    public void createTimers(ActivityExecution execution, Assignment assignment) {
        Date dueDateVar = getDueDate(execution, assignment);
        if (dueDateVar != null) {
            assignment.setDueDate(dueDateVar);
            EntityLoadInfo userLoadInfo = EntityLoadInfo.create(assignment.getUser());
            Map<String, String> params = new HashMap<>();
            params.put("user", userLoadInfo.toString());
            WfHelper.getTimerManager().addTimer(assignment.getCard(), execution, dueDateVar,
                    getClass(), EndorsementTimerAction.class, params);
        }
    }

    protected Date getDueDate(ActivityExecution execution, Assignment assignment) {
        return (Date) execution.getVariable("dueDate");
    }

    public void removeTimers(ActivityExecution execution) {
        removeTimers(execution, null);
    }

    public void removeTimers(ActivityExecution execution, Assignment assignment) {
        if (assignment == null)
            WfHelper.getTimerManager().removeTimers(execution);
        else
            WfHelper.getTimerManager().removeTimers(execution, assignment);
//        execution.removeVariable("dueDate");

        EntityManager em = AppBeans.get(Persistence.class).getEntityManager();
        List<CardInfo> cardInfos = em.createQuery(
                "select ci from wf$CardInfo ci " +
                        "   where ci.jbpmExecutionId = ?1 " +
                        "   and ci.activity = ?2 " +
                        "   and ci.user.id = ?3", CardInfo.class)
                .setParameter(1, execution.getId())
                .setParameter(2, execution.getActivityName())
                .setParameter(3, AppBeans.get(UserSessionSource.class).currentOrSubstitutedUserId())
                .getResultList();
        for (CardInfo cardInfo : cardInfos) {
            em.remove(cardInfo);
        }
    }
}