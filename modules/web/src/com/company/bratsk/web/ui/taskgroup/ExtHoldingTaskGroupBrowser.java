/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.taskgroup;

import com.company.bratsk.entity.ExtTaskGroupTask;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.thesis.core.entity.TaskGroup;
import com.haulmont.thesis_holding.web.ui.taskgroup.HoldingTaskGroupBrowser;

import javax.inject.Inject;
import java.util.*;

public class ExtHoldingTaskGroupBrowser extends HoldingTaskGroupBrowser {

    @Inject
    protected Notifications notifications;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        taskGroupsTable.removeAction("remove");
        taskGroupsTable.addAction(new RemoveAction(taskGroupsTable) {
            @Override
            protected void afterRemove(Set selected) {
                super.afterRemove(selected);
            }

            @Override
            public void actionPerform(Component component) {

                List<String> denyRemovedList = getDenyRemovedList(new ArrayList<>(taskGroupsTable.getSelected()));
                if (!denyRemovedList.isEmpty()) {
                    notifications.create(Notifications.NotificationType.WARNING).withCaption(messages
                            .getMessage(this.getClass(), "ExtHoldingTaskGroupBrowser.attention"))
                            .withDescription(messages.getMessage(this.getClass(), "warningFirstPart") + " " +
                                    denyRemovedList.toString().substring(1, denyRemovedList.toString().length()-1)+
                                    ". " + messages.getMessage(this.getClass(), "warningSecondPart")).show();
                } else {
                    super.actionPerform(component);
                }
            }
        });
    }

    private List<String> getDenyRemovedList(List<TaskGroup> taskGroupIds) {
        List<String> taskGroupNamesCanNotBeDeleted = new ArrayList<>();
        for (TaskGroup taskGroup : taskGroupIds) {
            LoadContext<ExtTaskGroupTask> loadContext = new LoadContext<>(ExtTaskGroupTask.class);
            loadContext.setQueryString("select tgt from bratsk$ExtTaskGroupTask tgt where tgt.taskGroup.id = :tgId" +
                    " and tgt.responsibleExecutor = true")
                    .setParameter("tgId", taskGroup.getId());
            if (getDsContext().getDataSupplier().getCount(loadContext) > 0) {
                taskGroupNamesCanNotBeDeleted.add(taskGroup.getName());
            }
        }
        return taskGroupNamesCanNotBeDeleted;
    }
}
