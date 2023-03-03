/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.relationtypempa;

import com.company.bratsk.entity.BratskConstants;
import com.company.bratsk.entity.RelatedCards;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.company.bratsk.entity.RelationTypeMPA;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;


public class RelationTypeMPABrowse extends AbstractLookup {

    @Named("relationTypeTable.create")
    private CreateAction createAction;
    @Named("relationTypeTable.edit")
    private EditAction editAction;
    @Inject
    protected Table<RelationTypeMPA> relationTypeTable;
    @Inject
    protected Datasource<RelationTypeMPA> relationTypesDs;
    @Inject
    protected Button removeBtn;
    @Inject
    protected Notifications notifications;
    protected List<UUID> relationTypeIdsFromDb = Arrays.asList(BratskConstants.StatusNameConstants.ACTION_STOPPED,
            BratskConstants.StatusNameConstants.EXPIRED, BratskConstants.StatusNameConstants.ACTIVE_ID,
            BratskConstants.StatusNameConstants.CANCELED, BratskConstants.StatusNameConstants.PROJECT_ID,
            BratskConstants.StatusNameConstants.ACTIVE_WITH_MODIFICATIONS, BratskConstants.ActionNameConstants.CANCELS,
            BratskConstants.ActionNameConstants.ADD_MODIFICATIONS, BratskConstants.ActionNameConstants.STOPS_ACTION,
            BratskConstants.ActionNameConstants.RECOGNIZES_AS_INVALID);


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        createAction.setAfterCommitHandler(event -> itemChanged());
        editAction.setAfterCommitHandler(event -> itemChanged());

        relationTypeTable.removeAction("remove");
        relationTypeTable.addAction(new RemoveAction(relationTypeTable) {
            @Override
            protected void afterRemove(Set selected) {
                super.afterRemove(selected);
                relationTypesDs.refresh();
            }

            @Override
            public void actionPerform(Component component) {
                if (relationTypeTable.getSingleSelected() != null
                        && (existRelatedCardsByType(relationTypeTable.getSingleSelected().getId()) ||
                        relationTypeIdsFromDb.contains(relationTypeTable.getSingleSelected().getId()))) {
                    notifications.create(Notifications.NotificationType.HUMANIZED)
                            .withCaption(getMessage("cantRemoveUsedRelationTypes")).show();
                } else {
                    super.actionPerform(component);
                }
            }
        });
    }

    @Override
    public void ready() {
        super.ready();
        relationTypesDs.addItemChangeListener(event -> {
            removeBtn.setEnabled(event.getItem() != null
                    && !relationTypeIdsFromDb.contains(event.getItem().getId()));
        });
    }

    protected void itemChanged() {
        relationTypesDs.refresh();
    }

    protected boolean existRelatedCardsByType(UUID relationTypeId) {
        LoadContext<RelatedCards> context = new LoadContext(RelatedCards.class);
        context.setQueryString("select rc from bratsk$RelatedCards rc where rc.sourceRelationType.id " +
                "= :relationType or rc.relatedRelationType.id = :relationType")
                .setParameter("relationType", relationTypeId);
        return getDsContext().getDataSupplier().getCount(context) > 0;
    }

}