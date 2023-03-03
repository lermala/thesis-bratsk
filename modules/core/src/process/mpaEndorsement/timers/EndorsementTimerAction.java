/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */
package process.mpaEndorsement.timers;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.thesis.core.app.ThesisMailWorker;
import com.haulmont.thesis.core.entity.TsCardInfo;
import com.haulmont.thesis.core.timer.ThesisAssignmentTimerAction;
import com.haulmont.workflow.core.entity.CardInfo;
import com.haulmont.workflow.core.timer.TimerActionContext;
import groovy.lang.Binding;

import java.util.HashMap;
import java.util.Map;

public class EndorsementTimerAction extends ThesisAssignmentTimerAction {

    protected String defaultScript = "scripts/OverdueAssignmentNotification.groovy";

    protected CardInfo createCardInfo(TimerActionContext context, User user, int type, String activityName, String script) {
        Metadata metadata = AppBeans.get(Metadata.NAME);
        Messages messages = AppBeans.get(Messages.NAME);
        TsCardInfo ci = metadata.create(TsCardInfo.class);
        ci.setCard(context.getCard());
        ci.setType(type);
        ci.setUser(user);
        ci.setJbpmExecutionId(context.getJbpmExecutionId());
        ci.setActivity(activityName);

        if (type == CardInfo.TYPE_OVERDUE) {
            ci.setTitle(messages.getMessage("com.haulmont.thesis.core.notification", "notification.defaultOverdueTitle"));
        }

        String subject;
        try {
            Map<String, Object> bindingParams = new HashMap<>();
            bindingParams.put("card", context.getCard());
            bindingParams.put("dueDate", context.getDueDate());
            bindingParams.put("user", user);

            Binding binding = new Binding(bindingParams);
            Scripting scripting = AppBeans.get(Scripting.NAME);
            Resources resources = AppBeans.get(Resources.NAME);

            Map<String, String> scriptResults = scripting.evaluateGroovy(resources.getResourceAsString(script), binding);
            subject = scriptResults.get("subject");
        } catch (Exception e) {
            log.error("Unable to get subject for cardInfo", e);
            subject = "Overdue stage of endorsement process";
        }

        ci.setDescription(subject);

        Persistence persistence = AppBeans.get(Persistence.NAME);
        EntityManager em = persistence.getEntityManager();
        em.persist(ci);

        return ci;
    }

    protected void sendEmail(TimerActionContext context, User user, String script) {
        try {
            Map<String, Object> bindingParams = new HashMap<>();
            bindingParams.put("card", context.getCard());
            bindingParams.put("dueDate", context.getDueDate());
            bindingParams.put("user", user);

            Binding binding = new Binding(bindingParams);
            Scripting scripting = AppBeans.get(Scripting.NAME);
            Resources resources = AppBeans.get(Resources.NAME);
            Map<String, String> scriptResults = scripting.evaluateGroovy(resources.getResourceAsString(script), binding);
            String subject = scriptResults.get("subject");
            String body = scriptResults.get("body");

            ThesisMailWorker thesisMailWorker = AppBeans.get(ThesisMailWorker.NAME);
            thesisMailWorker.sendEmail(user, subject, body);
        } catch (Exception e) {
            log.error("Unable to send email to " + user.getEmail(), e);
        }
    }

    @Override
    protected void execute(TimerActionContext context, User user) {
        if (!makesSense(context, user)) return;
        String script = context.getCard().getProc().getMessagesPack().replace(".", "/") + "/OverdueAssignmentNotification.groovy";
        if (AppBeans.get(Resources.class).getResourceAsString(script) == null) script = defaultScript;
        createCardInfo(context, user, CardInfo.TYPE_OVERDUE, context.getActivity(), script);
        sendEmail(context, user, script);
    }
}