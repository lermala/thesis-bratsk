/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.taskgrouptask;

import com.company.bratsk.entity.ExtTaskGroupTask;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.haulmont.cuba.core.entity.CategoryAttribute;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.AbstractAction;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.web.App;
import com.haulmont.thesis.core.entity.Task;
import com.haulmont.thesis.core.entity.TaskGroup;
import com.haulmont.thesis.core.entity.TaskGroupAttachment;
import com.haulmont.thesis.core.entity.TaskGroupTask;
import com.haulmont.thesis.web.DocflowApp;
import com.haulmont.thesis.web.ui.taskgrouptask.TaskGroupTaskColumnGenerator;
import com.haulmont.thesis_holding.web.ui.taskgrouptask.HoldingTaskGroupTaskFrame;
import com.haulmont.workflow.core.entity.Attachment;
import com.haulmont.workflow.core.entity.CardAttachment;
import com.haulmont.workflow.core.entity.CardRole;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class ExtHoldingTaskGroupTaskFrame extends HoldingTaskGroupTaskFrame {

    @Inject
    protected Notifications notifications;
    private Task taskWithResponsibleExecutor;
    private Set<Task> existedTasks = new HashSet<>();

    public static final String RESPONSIBLE_EXECUTOR = "responsibleExecutor";

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        tasksTable.removeAction("remove");
        tasksTable.addAction(new RemoveAction(tasksTable, false) {
            @Override
            protected void doRemove(Set selected, boolean autocommit) {
                boolean remove = true;
                TaskGroupTask taskGroupTask = (TaskGroupTask) selected.iterator().next();
                Collection<Window> openWindows = App.getInstance().getWindowManager().getOpenWindows();
                for (Window window : openWindows) {
                    if (taskGroupTask.getTask() != null && StringUtils.contains(window.getCaption(),
                            taskGroupTask.getTask().getNum())) {
                        remove = false;
                        break;
                    }
                }
                if (remove) {
                    if (taskGroupTask.getTask() != null || (taskGroupTask.getTask() != null
                            && BooleanUtils.isTrue(((ExtTaskGroupTask) taskGroupTask).getResponsibleExecutor()))) {
                        notifications.create(Notifications.NotificationType.WARNING)
                                .withCaption(getMessage("ExtTaskGroupTask.attention"))
                                .withDescription(getMessage("ExtTaskGroupTask.onRemoveDeny"))
                                .show();
                    } else {
                        super.doRemove(selected, autocommit);
                    }
                } else {
                    showNotification(messages.getMessage(getClass(),
                            "messages.openInTheActiveTab"), NotificationType.WARNING);
                }
            }

            @Override
            public boolean isEnabled() {
                return isEditable && super.isEnabled();
            }
        });

        tasksTable.removeAction("addTask");
        tasksTable.addAction(new AddExistsTaskGroupTaskAction());
    }

    @Override
    protected void initTaskGroupTaskTable() {
        super.initTaskGroupTaskTable();
        iniTaskGroupTaskTableColumn(RESPONSIBLE_EXECUTOR, createTaskGroupTaskTableResponsibleExecutorColumnGenerator());
    }

    protected TaskGroupTaskColumnGenerator createTaskGroupTaskTableResponsibleExecutorColumnGenerator() {
        return new TaskGroupTaskColumnGenerator() {
            @Override
            protected Component createComponent(TaskGroupTask taskGroupTask) {
                CheckBox component = componentsFactory.createComponent(CheckBox.class);

                component.addValueChangeListener((e) -> {
                    ((ExtTaskGroupTask) ExtHoldingTaskGroupTaskFrame.this.taskGroupTaskDs
                            .getItemNN(taskGroupTask.getId())).setResponsibleExecutor(e.getValue());
                    Collection<TaskGroupTask> dsTaskGroupTaskItems = ExtHoldingTaskGroupTaskFrame.this.
                            taskGroupTaskDs.getItems();
                    dsTaskGroupTaskItems.stream().filter(tgt ->
                            BooleanUtils.isTrue(((ExtTaskGroupTask) tgt).getResponsibleExecutor())
                                    && !tgt.getId().equals(taskGroupTask.getId())).findFirst().ifPresent(tgt -> {
                        Component componentFromMap = map.get(tgt.getId());
                        if (componentFromMap instanceof CheckBox && e.getValue() != null && e.getValue()) {
                            ((CheckBox) componentFromMap).setValue(false);
                        }
                    });
                });

                Collection<TaskGroupTask> dsTaskGroupTaskItems = ExtHoldingTaskGroupTaskFrame.this.
                        taskGroupTaskDs.getItems();

                for (TaskGroupTask dsTaskGroupTaskItem : dsTaskGroupTaskItems) {
                    Component componentFromMap = map.get(dsTaskGroupTaskItem.getId());
                    if (BooleanUtils.isTrue(((ExtTaskGroupTask) dsTaskGroupTaskItem).getResponsibleExecutor())) {
                        boolean abilityToEdit = dsTaskGroupTaskItems.stream().noneMatch(tgt ->
                                BooleanUtils.isTrue(((ExtTaskGroupTask) tgt).getResponsibleExecutor())
                                        && !tgt.getId().equals(taskGroupTask.getId()) && tgt.getTask() != null);
                        if (componentFromMap instanceof CheckBox) {
                            ((CheckBox) componentFromMap).setEditable(abilityToEdit);
                        }
                        component.setEditable(abilityToEdit);
                    }
                }

                component.setValue(BooleanUtils.isTrue(((ExtTaskGroupTask) ExtHoldingTaskGroupTaskFrame.this.
                        taskGroupTaskDs.getItemNN(taskGroupTask.getId())).getResponsibleExecutor()));

                if (dsTaskGroupTaskItems.stream().anyMatch(tgt -> tgt.getTask() != null)) {
                    component.setEditable(false);
                }

                if (!ExtHoldingTaskGroupTaskFrame.this.isEditable || taskGroupTask.getTask() != null) {
                    component.setEditable(false);
                }

                return component;
            }
        };
    }

    protected class AddExistsTaskGroupTaskAction extends AbstractAction {

        public AddExistsTaskGroupTaskAction() {
            super("addTask");
        }

        public void actionPerform(Component component) {
            final TaskGroupTask tgt = metadata.create(TaskGroupTask.class);
            tgt.setTaskGroup(taskGroupDs.getItem());

            TaskGroupTask tempTgt;
            Task tempTask;

            LinkedList<Task> tasks = new LinkedList<>();

            for (UUID uuid : taskGroupTaskDs.getItemIds()) {
                tempTgt = taskGroupTaskDs.getItemNN(uuid);
                tempTask = tempTgt.getTask();
                if (tempTask != null)
                    tasks.add(tempTask);
            }
            HashMap<String, Object> params = new HashMap<>();
            params.put("taskGroupTaskFrameLookup", "true");

            tasks.addAll(getExcludedTasks());
            params.put("exclItems", tasks);

            WindowParams.MULTI_SELECT.set(params, false);

            TaskGroup taskGroup = taskGroupDs.getItem();
            if (taskGroup != null && taskGroup.getParentCard() != null) {
                params.put("exclItem", taskGroup.getParentCard());
            }
            openLookup("tm$Task.browse", items -> {
                if (items != null && items.size() > 0) {
                    Task task = (Task) items.iterator().next();
                    Task reloadedTask = getDsContext().getDataSupplier().reload(task, "edit");
                    tgt.setTask(reloadedTask);
                    tgt.syncWithTask();
                    tgt.setInitiator(reloadedTask.getInitiator());

                    if (reloadedTask.getRoles() != null) {
                        for (CardRole cardRole : reloadedTask.getRoles()) {
                            if (Task.CONTROLLER_ROLE.equals(cardRole.getCode())) {
                                tgt.setController(cardRole.getUser());
                            }
                            if (Task.OBSERVER_ROLE.equals(cardRole.getCode())) {
                                tgt.setObserver(cardRole.getUser());
                            }
                        }
                    }
                    taskGroupTaskDs.addItem(tgt);
                    existedTasks.add(task);
                    commitExistedTasksForHierarchy();

                }
            }, WindowManager.OpenType.THIS_TAB, params);
        }

        @Override
        public boolean isVisible() {
            return !isTemplate && super.isVisible();
        }

        @Override
        public String getCaption() {
            return getMessage("table.addTask");
        }

        @Override
        public boolean isEnabled() {
            return isEditable && super.isEnabled();
        }
    }

    protected List<Task> getExcludedTasks() {
        LoadContext<Task> taskLoadContext = new LoadContext<>(Task.class);
        taskLoadContext.setQueryString("select t from tm$Task t where t.deleteTs is null and (t.state not in " +
                "(',New,') or t.parentCard is not null)");
        taskLoadContext.setView("browse");
        List<Task> tasks = dataService.loadList(taskLoadContext);

        LoadContext<Task> loadContextFromTaskGroupTask = new LoadContext<>(Task.class);
        loadContextFromTaskGroupTask.setQueryString("select tgt.task from bratsk$ExtTaskGroupTask tgt where " +
                "tgt.responsibleExecutor is not null and tgt.responsibleExecutor = false" +
                " and tgt.task is not null");
        List<Task> tasksFromTgt = dataService.loadList(loadContextFromTaskGroupTask);

        tasks.addAll(tasksFromTgt);

        return tasks;
    }

    @Override
    protected Task createTask(TaskGroupTask tgt) {
        Collection<TaskGroupTask> dsTaskGroupTaskItems = ExtHoldingTaskGroupTaskFrame.this.taskGroupTaskDs.getItems();
        Task task = taskmanService.createTask(tgt);
        task.setOrganization(taskGroup.getOrganization());
        Optional<TaskGroupTask> tgtWithResponsibleExecutor = dsTaskGroupTaskItems.stream().filter(t ->
                BooleanUtils.isTrue(((ExtTaskGroupTask) t).getResponsibleExecutor())).findFirst();
        if (tgtWithResponsibleExecutor.isPresent()) {
            if (tgtWithResponsibleExecutor.get().getTask() != null) {
                taskWithResponsibleExecutor = tgtWithResponsibleExecutor.get().getTask();
            }
            if (tgtWithResponsibleExecutor.get().getId().equals(tgt.getId())) {
                task.setParentCard(taskGroupDs.getItem().getParentCard());
                taskWithResponsibleExecutor = task;
            } else {
                task.setParentCard(taskWithResponsibleExecutor);
            }
        } else {
            task.setParentCard(taskGroupDs.getItem().getParentCard());
        }
        return task;
    }

    @Override
    protected void createTasks(List<TaskGroupTask> taskGroupTasks) {
        Task task;
        final Set<Task> allTasks = new HashSet<>();
        Set<Entity> toCommit = new HashSet<>();
        Set<Entity> toCommitTGT = new HashSet<>();
        if (!((Window.Editor) getHostScreen()).commit())
            return;

        TaskGroup taskGroup = taskGroupDs.getItem();

        LinkedList<TaskGroupTask> orderedTaskGroupTasks = orderedTgts(taskGroupTasks);
        for (TaskGroupTask tgt : orderedTaskGroupTasks) {
            if (tgt.getTask() != null) continue;
            toCommit.clear();
            LoadContext<TaskGroupTask> ctx = new LoadContext<>(TaskGroupTask.class);
            ctx.setView("taskGroupTask-edit");
            ctx.setId(tgt.getId());
            tgt = dataService.load(ctx);
            if (tgt == null) continue;
            if (tgt.getInitiator() == null) {
                TaskGroup tg = taskGroupDs.getItem();
                if (tg != null)
                    tgt.setInitiator(tg.getInitiator());
            }
            task = createTask(tgt);

            toCommit.add(task);
            toCommit.addAll(task.getRoles());

            if (!hasResponsibleExecutor()) {
                if (taskGroup.getAttachments() != null) {
                    for (TaskGroupAttachment attachment : taskGroup.getAttachments()) {
                        CardAttachment cardAttachment = cloneAttachmentFromExtendedEntity(attachment);
                        cardAttachment.setCard(task);
                        toCommit.add(cardAttachment);
                    }
                }
            }

            tgt.setTask(task);
            tgt.syncWithTask();
            toCommitTGT.add(tgt);
            initDefaultAttributeValuesFromExtendedEntity(task, toCommit);
            CommitContext commitContext = new CommitContext(toCommit);
            dataService.commit(commitContext);
            allTasks.add(task);
            createdTasks = true;
            getComponentNN("createFromPatternButton").setVisible(false);
        }
        commitExistedTasksForHierarchy();
        commitTaskGroupTask(toCommitTGT);
        Collection tgTasks = Collections2.transform(taskGroup.getTaskGroupTasks(), TaskGroupTask::getTask);
        Collection excludedTasks = Collections2.filter(tgTasks,
                (Predicate<Task>) input -> input != null && !allTasks.contains(input));

        taskmanService.startTaskProcess(taskGroup, new ArrayList<Task>(excludedTasks), null);
        refreshContextFromExtendedEntity(toCommitTGT);
        DocflowApp.getInstance().getMainWindow().reloadAppFolders();
        sortDatasource();
    }

    private CardAttachment cloneAttachmentFromExtendedEntity(Attachment srcAttachment) {
        CardAttachment newAttachment = metadata.create(CardAttachment.class);
        newAttachment.setFile(srcAttachment.getFile());
        newAttachment.setName(srcAttachment.getName());
        newAttachment.setAttachType(srcAttachment.getAttachType());
        return newAttachment;
    }

    private void initDefaultAttributeValuesFromExtendedEntity(Task task, Set<Entity> toCommit) {
        List<CategoryAttribute> attributes = runtimePropertiesService
                .getCategoryAttributesByCategory(task.getCategory());
        for (CategoryAttribute attribute : attributes) {
            toCommit.add(createAttributeWithDefaultValue(attribute, task));
        }
    }

    private void refreshContextFromExtendedEntity(Set<Entity> tasks) {
        List<TaskGroupTask> groupTasks = taskGroupDs.getItem().getTaskGroupTasks();
        for (Entity task : tasks) {
            TaskGroupTask loadTask = getDsContext().getDataSupplier().load(
                    new LoadContext<>(TaskGroupTask.class).setView("taskGroupTask-edit").setId(task.getId()));
            groupTasks.remove(task);
            groupTasks.add(loadTask);
        }
        if (taskGroupTaskDs.getItem() != null) {
            UUID idToReload = taskGroupTaskDs.getItem().getId();
            taskGroupTaskDs.setItem(taskGroupTaskDs.getItem(idToReload));
        }
        tasksTable.refresh();
    }

    protected LinkedList<TaskGroupTask> orderedTgts(List<TaskGroupTask> defaultListOfTgt) {
        LinkedList<TaskGroupTask> orderedList = new LinkedList<>();
        defaultListOfTgt.stream().sorted((o1, o2) -> {
            Boolean o1B = BooleanUtils.isTrue(((ExtTaskGroupTask) o1).getResponsibleExecutor());
            Boolean o2B = BooleanUtils.isTrue(((ExtTaskGroupTask) o2).getResponsibleExecutor());
            return o1B.compareTo(o2B);
        }).forEachOrdered(orderedList::add);

        Collections.reverse(orderedList);

        return orderedList;
    }

    protected boolean hasResponsibleExecutor() {
        Collection<TaskGroupTask> dsTaskGroupTaskItems = ExtHoldingTaskGroupTaskFrame.this.taskGroupTaskDs.getItems();
        return dsTaskGroupTaskItems.stream().anyMatch(tgt ->
                BooleanUtils.isTrue(((ExtTaskGroupTask) tgt).getResponsibleExecutor()));
    }

    private void commitExistedTasksForHierarchy() {
        Set<Entity> toCommit = new HashSet<>();
        if (!existedTasks.isEmpty()) {
            Optional<TaskGroupTask> parentWithResponsible = ExtHoldingTaskGroupTaskFrame.this.
                    taskGroupTaskDs.getItems().stream()
                    .filter(tgt -> BooleanUtils.isTrue(((ExtTaskGroupTask) tgt).getResponsibleExecutor())).findFirst();
            for (Task existedTask : existedTasks) {
                if (parentWithResponsible.isPresent()) {
                    if (parentWithResponsible.get().getTask() != null) {
                        existedTask.setParentCard(parentWithResponsible.get().getTask());
                    } else if (taskWithResponsibleExecutor != null) {
                        existedTask.setParentCard(taskWithResponsibleExecutor);
                    }
                    toCommit.add(existedTask);
                    CommitContext commitContext = new CommitContext(toCommit);
                    dataService.commit(commitContext);
                }
            }
        }
    }

}
