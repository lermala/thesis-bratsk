/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.core.listener;

import com.company.bratsk.entity.ExtTask;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import com.haulmont.thesis.core.app.UserSessionTools;
import com.haulmont.thesis.core.process.TaskManagementConstants;
import com.haulmont.workflow.core.app.WfUtils;
import com.haulmont.workflow.core.entity.Assignment;
import com.haulmont.workflow.core.entity.CardInfo;
import com.haulmont.workflow.core.entity.CardProc;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.List;
import java.util.Set;

/**
 * @author IGoridko
 */
@Component("bratsk_TaskListener")
public class ExtTaskEntityListener implements AfterUpdateEntityListener<ExtTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtTaskEntityListener.class);

    private static final String STATE_NEW = "New"; //Новое
    private static final String STATE_FINISHED = "Finished"; //Завершено
    private static final String STATE_FINISHED_BY_INITIATOR = "FinishedByInitiator"; //Завершено инициатором
    private static final String STATE_CANCELED = "Canceled"; //Отменено
    private static final String MESSAGES_PACKAGE_NAME = "com/company/bratsk/core";

    @Inject
    protected PersistenceTools persistenceTools;
    @Inject
    private Persistence persistence;
    @Inject
    protected Messages messages;

    @Override
    public void onAfterUpdate(ExtTask entity, Connection connection) {
        Set<String> dirtyFields = persistenceTools.getDirtyFields(entity);

        if (AppBeans.get(UserSessionTools.class).isCurrentUserInProcRole(
                entity,
                TaskManagementConstants.PROC_NAME,
                TaskManagementConstants.PROC_ROLE_CONTROLLER
        )
                && WfUtils.isCardInState(entity, STATE_FINISHED)
                && entity.getConfirmRequired()
                && dirtyFields.contains("state")
                && BooleanUtils.isNotTrue(entity.getSubtasksFinished())
        ) {
            startCompletingAllSubtasksWithOutcome(entity, STATE_FINISHED);
        }

        if (AppBeans.get(UserSessionTools.class).isCurrentUserInProcRole(
                entity,
                TaskManagementConstants.PROC_NAME,
                TaskManagementConstants.PROC_ROLE_INITIATOR
        )
                && WfUtils.isCardInStateList(entity, STATE_FINISHED, STATE_FINISHED_BY_INITIATOR)
                && dirtyFields.contains("state")
                && BooleanUtils.isNotTrue(entity.getSubtasksFinished())
        ) {
            startCompletingAllSubtasksWithOutcome(entity, STATE_FINISHED_BY_INITIATOR);
        }
    }

    private void startCompletingAllSubtasksWithOutcome(ExtTask extTask, String outcome) {
        LOGGER.debug("Я startCompletingAllSubtasksWithOutcome " + extTask.getTaskName());

        Transaction transaction = persistence.createTransaction();
        try {
            EntityManager entityManager = persistence.getEntityManager();
            completeAllSubtasksWithOutcome(extTask, outcome, entityManager);
            entityManager.merge(extTask);
            transaction.commit();
        } finally {
            transaction.end();
        }
    }

    private void completeAllSubtasksWithOutcome(ExtTask extTask, String outcome, EntityManager entityManager) {
        for (ExtTask subtask : loadChildTasks(extTask, entityManager)) {
            if (BooleanUtils.isNotTrue(WfUtils.isCardInStateList(subtask,
                    STATE_NEW,
                    STATE_CANCELED,
                    STATE_FINISHED,
                    STATE_FINISHED_BY_INITIATOR))) {
                LOGGER.debug("Я completeAllSubtasksWithOutcome " + subtask.getTaskName());

                completeAllSubtasksWithOutcome(subtask, outcome, entityManager);

                if (BooleanUtils.isNotTrue(WfUtils.isCardInStateList(
                        subtask,
                        STATE_FINISHED,
                        STATE_FINISHED_BY_INITIATOR,
                        STATE_CANCELED
                )) && BooleanUtils.isFalse(extTask.getSubtasksFinished())) {
                    completeTask(subtask, outcome, entityManager);
                }
            }
        }
    }

    private void completeTask(ExtTask extTask, String outcome, EntityManager entityManager) {
        LOGGER.debug("Я completeTask " + extTask.getTaskName());
        String taskState = String.format(",%s,", outcome);

        extTask.setJbpmProcessId(null);
        extTask.setState(taskState);
        extTask.setSubtasksFinished(true);
        for (CardProc cardProc : extTask.getProcs()) {
            cardProc.setActive(false);
            cardProc.setState(taskState);
            entityManager.merge(cardProc);
        }

        for (Assignment assignment : loadActiveTaskAssignments(extTask, entityManager)) {
            finishActiveTaskAssignment(assignment, entityManager);
            createTaskCompletingNotification(extTask, assignment, entityManager);
        }

        entityManager.merge(extTask);
    }

    private List<ExtTask> loadChildTasks(ExtTask extTask, EntityManager entityManager) {
        // language=JPAQL
        String query = "select et from bratsk$ExtTask et " +
                "           where et.parentCard.id = :taskId " +
                "           and et.deleteTs is null ";
        return entityManager.createQuery(query, ExtTask.class)
                .setParameter("taskId", extTask.getId())
                .setView(extTask.getClass(), "edit")
                .getResultList();
    }

    private List<Assignment> loadActiveTaskAssignments(ExtTask extTask, EntityManager entityManager) {
        // language=JPAQL
        String query = "select a from wf$Assignment a " +
                "           where a.card.id = :cardId " +
                "           and a.finished is null " +
                "           and a.card.deleteTs is null " +
                "           and a.user is not null ";
        return entityManager.createQuery(query, Assignment.class)
                .setParameter("cardId", extTask.getId())
                .getResultList();
    }

    private void finishActiveTaskAssignment(Assignment assignment, EntityManager entityManager) {
        assignment.setName(TaskManagementConstants.STATE_COMPLETED);
        assignment.setFinished(AppBeans.get(TimeSource.class).currentTimestamp());
        assignment.setFinishedByUser(AppBeans.get(UserSessionSource.class).getUserSession().getUser());
        assignment.setOutcome("Ok");
        assignment.setComment(messages.getMessage(MESSAGES_PACKAGE_NAME, "subtaskCompletedAssignmentComment"));
        entityManager.merge(assignment);
    }

    private void createTaskCompletingNotification(ExtTask extTask, Assignment assignment, EntityManager entityManager) {
        CardInfo cardInfo = AppBeans.get(Metadata.class).create(CardInfo.class);
        cardInfo.setCard(extTask);
        cardInfo.setType(0);
        cardInfo.setUser(assignment.getUser());
        cardInfo.setJbpmExecutionId(TaskManagementConstants.PROC_NAME);
        cardInfo.setDescription(String.format(
                messages.getMessage(MESSAGES_PACKAGE_NAME, "completedSubtaskCardInfoDescription"),
                extTask.getNum(),
                extTask.getTaskName()
        ));
        entityManager.persist(cardInfo);
    }

}
