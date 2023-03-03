/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.task;

import com.company.bratsk.entity.ExtTask;
import com.company.bratsk.entity.ExtTaskGroupTask;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.thesis.core.entity.Task;
import com.haulmont.thesis.core.entity.TaskGroup;
import com.haulmont.thesis.core.entity.TaskGroupTask;
import com.haulmont.thesis.core.enums.TaskState;
import com.haulmont.thesis.core.process.TaskManagementConstants;
import com.haulmont.thesis.web.ui.basic.editor.CardHeaderFragment;
import com.haulmont.thesis_holding.web.ui.task.HoldingTaskEditor;
import com.haulmont.workflow.core.app.WfUtils;
import com.haulmont.workflow.core.entity.Card;
import com.hazelcast.util.CollectionUtil;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.haulmont.thesis.web.ui.basic.editor.CardHeaderFragment.TitleTagType.NEGATIVE;

public class ExtHoldingTaskEditor<T extends Task> extends HoldingTaskEditor<T> {

    public static final String RESPONSIBLE_EXECUTOR_CAPTION_HEADER = "headerResponsibleExecutorCaption";
    public static final String RESPONSIBLE_EXECUTOR_COMPONENT_ID_HEADER = "headerResponsibleExecutor";
    public static final String CO_EXECUTOR_CAPTION_HEADER = "headerCoExecutorCaption";
    public static final String CO_EXECUTOR_COMPONENT_ID_HEADER = "headerCoExecutor";

    @Override
    protected void initHeaderFragmentContent(CardHeaderFragment cardHeaderFragment) {
        cardHeaderFragment.addTitleTag("headerTitileTagOverdue", getMessage("header.overdue"), NEGATIVE);
        cardHeaderFragment.setTitleTagVisible("headerTitileTagOverdue", false);
        cardHeaderFragment.addDetailsInfoItem("headerNumCaption", getMessage("header.num"),
                "headerNum", cardDs, "num");
        cardHeaderFragment.addDetailsInfoItem("headerCreateDateTimeCaption",
                getMessage("header.createDate"), "headerCreateDateTime", cardDs, "createDateTime");
        cardHeaderFragment.addDetailsInfoItemHolder("headerStateCaption",
                getMessage("header.state"), "headerStateHolder");
        cardHeaderFragment.addDetailsInfoItem("headerInitiatorCaption",
                getMessage("header.initiator"), "headerInitiator", "");
        cardHeaderFragment.setDetailsInfoItemVisible("headerInitiatorCaption",
                "headerInitiator", false);

        cardHeaderFragment.addDetailsInfoItem(RESPONSIBLE_EXECUTOR_CAPTION_HEADER,
                getMessage("ExtHoldingTaskEditor.responsibleExecutor"),
                RESPONSIBLE_EXECUTOR_COMPONENT_ID_HEADER, "");
        cardHeaderFragment.setDetailsInfoItemVisible(RESPONSIBLE_EXECUTOR_CAPTION_HEADER,
                RESPONSIBLE_EXECUTOR_COMPONENT_ID_HEADER, false);
        cardHeaderFragment.addDetailsInfoItem(CO_EXECUTOR_CAPTION_HEADER,
                getMessage("ExtTaskGroupTask.coExecutor"),
                CO_EXECUTOR_COMPONENT_ID_HEADER, "");
        cardHeaderFragment.setDetailsInfoItemVisible(CO_EXECUTOR_CAPTION_HEADER,
                CO_EXECUTOR_COMPONENT_ID_HEADER, false);

        cardHeaderFragment.addDetailsInfoItem("headerExecutorCaption",
                getMessage("header.executor"), "headerExecutor", "");
        cardHeaderFragment.setDetailsInfoItemVisible("headerExecutorCaption",
                "headerExecutor", false);
        cardHeaderFragment.addDetailsInfoItem("headerControllerCaption",
                getMessage("header.controller"), "headerController", "");
        cardHeaderFragment.setDetailsInfoItemVisible("headerControllerCaption",
                "headerController", false);
    }

    private void setHeaderResponsibleExecutorVisible(boolean visible, String text) {
        setHeaderDetailsItemVisible(RESPONSIBLE_EXECUTOR_CAPTION_HEADER,
                RESPONSIBLE_EXECUTOR_COMPONENT_ID_HEADER, visible, text);
    }

    private void setHeaderCoExecutorVisible(boolean visible, String text) {
        setHeaderDetailsItemVisible(CO_EXECUTOR_CAPTION_HEADER,
                CO_EXECUTOR_COMPONENT_ID_HEADER, visible, text);
    }

    @Override
    protected void initHeaderParticipants() {
        super.initHeaderParticipants();
        T task = getItem();


        if (!isNewItem(task) && !TaskState.New.getId().equals(task.getState())) {
            User responsibleExecutor = taskGroupTaskWithResponsibleExecutor();
            Set<User> coExecutors = taskGroupTaskWithCoExecutors() == null || responsibleExecutor == null ?
                    null : taskGroupTaskWithCoExecutors().stream()
                    .filter(user -> !task.getExecutors().stream()
                            .map(User::getId).collect(Collectors.toList()).contains(user.getId()))
                    .filter(newUser->!newUser.getId().equals(responsibleExecutor.getId()))
                    .collect(Collectors.toSet());
            String responsibleExecutorName = responsibleExecutor != null ?
                    userFormatTools.formatOfficial(responsibleExecutor) : StringUtils.EMPTY;

            List<String> coExecutorsNames = coExecutors != null ?
                    coExecutors.stream().map(user -> userFormatTools.formatOfficial(user).trim()).collect(Collectors.toList())
                    : List.of("");

            String names = StringUtils.join(coExecutorsNames, ", ");
            setHeaderCoExecutorVisible(!names.isEmpty(), names);
            setHeaderResponsibleExecutorVisible(responsibleExecutor != null, responsibleExecutorName);
        }
    }

    private List<User> taskGroupTaskWithCoExecutors() {
        TaskGroup taskGroupEntity = taskGroupFromCurrent();

        if (taskGroupEntity != null) {
            List<TaskGroupTask> taskGroupTasks = taskGroupEntity.getTaskGroupTasks();
            return taskGroupTasks.stream().filter(x -> x.getTask() != null
                    && BooleanUtils.isFalse(((ExtTaskGroupTask) x).getResponsibleExecutor())
                    && x.getUser() != null).map(TaskGroupTask::getUser).collect(Collectors.toList());
        }

        return null;
    }

    private User taskGroupTaskWithResponsibleExecutor() {
        TaskGroup taskGroupEntity = taskGroupFromCurrent();

        if (taskGroupEntity != null) {
            List<TaskGroupTask> taskGroupTasks = taskGroupEntity.getTaskGroupTasks();
            Optional<TaskGroupTask> tgt = taskGroupTasks.stream().filter(x -> x.getTask() != null
                    && BooleanUtils.isTrue(((ExtTaskGroupTask) x).getResponsibleExecutor())
                    && x.getUser() != null).findFirst();
            if (tgt.isPresent()) {
                return tgt.get().getUser();
            }
        }

        return null;
    }

    private TaskGroup taskGroupFromCurrent() {
        T task = getItem();
        LoadContext<TaskGroup> taskGroupLoadContext = new LoadContext<>(TaskGroup.class);
        taskGroupLoadContext.setQueryString("select tgt.taskGroup from bratsk$ExtTaskGroupTask tgt" +
                " where tgt.task is not null and tgt.task.id = :taskId");
        taskGroupLoadContext.setView("edit");
        Card tmp = task;
        TaskGroup extTaskGroup = null;
        while (tmp instanceof Task) {
            taskGroupLoadContext.getQuery().setParameter("taskId", tmp.getId());
            extTaskGroup = dataService.load(taskGroupLoadContext);
            if (extTaskGroup != null &&
                    extTaskGroup.getTaskGroupTasks().stream()
                            .anyMatch(tgt -> ((ExtTaskGroupTask) tgt).getResponsibleExecutor())) {
                return extTaskGroup;
            }
            tmp = tmp.getParentCard();
        }
        return extTaskGroup;
    }

    @Override
    public void ready() {
        super.ready();
        configureConfirmRequiredCheckBox();
    }

    private void configureConfirmRequiredCheckBox() {
        confirmRequired.setVisible(isCurrentUserAdministratorOrTaskManagementController());
        confirmRequired.setEditable(isTaskNotInStateCompleted()
                && isCurrentUserAdministratorOrTaskManagementController()
        );
    }

    private boolean isCurrentUserAdministratorOrTaskManagementController() {
        return userSessionTools.isCurrentUserInProcRole(
                taskItem,
                TaskManagementConstants.PROC_NAME,
                TaskManagementConstants.PROC_ROLE_CONTROLLER
        )
                || userSessionTools.isCurrentUserAdministrator();
    }

    private boolean isTaskNotInStateCompleted() {
        return !WfUtils.isCardInStateList(
                taskItem,
                TaskManagementConstants.STATE_COMPLETED,
                "Finished",
                "FinishedByInitiator"
        );
    }

}
