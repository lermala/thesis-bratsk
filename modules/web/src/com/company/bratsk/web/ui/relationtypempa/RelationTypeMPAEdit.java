/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.relationtypempa;

import com.company.bratsk.entity.BratskConstants;
import com.company.bratsk.entity.RelatedCards;
import com.company.bratsk.entity.RelationTypeMPA;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import java.util.*;


public class RelationTypeMPAEdit<T extends RelationTypeMPA> extends AbstractEditor<T> {

    @Inject
    protected LookupPickerField<RelationTypeMPA> reverseName;
    @Inject
    protected CheckBox isAnAction;
    @Inject
    protected TextArea<String> comment;
    @Inject
    protected TextField<String> typeName;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionDatasource<RelationTypeMPA, UUID> reverseTypesDs;
    protected RelationTypeMPA prevReversRelationName = null;
    protected List<UUID> relationTypeIdsFromDb = Arrays.asList(BratskConstants.StatusNameConstants.ACTION_STOPPED,
            BratskConstants.StatusNameConstants.EXPIRED, BratskConstants.StatusNameConstants.ACTIVE_ID,
            BratskConstants.StatusNameConstants.CANCELED, BratskConstants.StatusNameConstants.PROJECT_ID,
            BratskConstants.StatusNameConstants.ACTIVE_WITH_MODIFICATIONS, BratskConstants.ActionNameConstants.CANCELS,
            BratskConstants.ActionNameConstants.ADD_MODIFICATIONS, BratskConstants.ActionNameConstants.STOPS_ACTION,
            BratskConstants.ActionNameConstants.RECOGNIZES_AS_INVALID);
    protected Boolean isFromParentCard = false;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.get("fromParent") != null) {
            isFromParentCard = (Boolean) params.get("fromParent");
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        initReverseNameLookup();
        prevReversRelationName = getItem().getReverseName();
        settingsForCollectionDatasourceQuery();
    }

    private void settingsForCollectionDatasourceQuery() {
        changeCollectionDatasourceQuery(BooleanUtils.isTrue(isAnAction.getValue()));
        isAnAction.addValueChangeListener(event -> {
            changeCollectionDatasourceQuery(BooleanUtils.isTrue(event.getValue()));
            reverseName.clear();
        });
    }

    private void changeCollectionDatasourceQuery(boolean flag) {
        reverseTypesDs.setQuery(queryChange(String.valueOf(flag)));
        reverseTypesDs.refresh();
    }

    private String queryChange(String condition) {
        return "select e from bratsk$RelationTypeMPA e where e.id <> :ds$relationTypeDs\n" +
                "                and (e.reverseName is null or e.reverseName.id = :ds$relationTypeDs)\n" +
                "                and e.isSystem <> true and e.isAnAction <> " + condition;
    }

    @Override
    public void ready() {
        super.ready();
        reverseName.setEditable(isEditable());
        isAnAction.setEditable(isEditable());
        createLookupOpenAction();
        if (isFromParentCard) {
            lockEditor();
        }
    }

    public void createLookupOpenAction() {
        reverseName.removeAction("lookup");

        PickerField.OpenAction action = new PickerField.OpenAction(reverseName);
        HashMap<String, Object> map = new HashMap<>();
        map.put("fromParent", true);
        action.setEditScreenParams(map);
        reverseName.addAction(action);
    }

    private void lockEditor() {
        typeName.setEditable(BooleanUtils.isFalse(isFromParentCard));
        reverseName.setEditable(BooleanUtils.isFalse(isFromParentCard));
        isAnAction.setEditable(BooleanUtils.isFalse(isFromParentCard));
        comment.setEditable(BooleanUtils.isFalse(isFromParentCard));
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (!super.postCommit(committed, close)) {
            return false;
        }
        processReverseTypes();
        return true;
    }

    protected void initReverseNameLookup() {
        PickerField.LookupAction reverseNameAction = (PickerField.LookupAction) reverseName.getAction("lookup");
        if (reverseNameAction != null) {
            Map<String, Object> lookupParams = new HashMap<>();
            lookupParams.put("optionsFor", getItem());
            reverseNameAction.setLookupScreenParams(lookupParams);
        }
    }

    protected void processReverseTypes() {
        RelationTypeMPA item = getItem();
        RelationTypeMPA currentReversName = item.getReverseName();

        List<RelationTypeMPA> toCommit = new ArrayList<>();
        if (currentReversName != null && !currentReversName.equals(prevReversRelationName)) {
            currentReversName.setReverseName(item);
            toCommit.add(currentReversName);
        }
        if (prevReversRelationName != null && !prevReversRelationName.equals(currentReversName)) {
            prevReversRelationName.setReverseName(null);
            toCommit.add(prevReversRelationName);
        }
        if (!toCommit.isEmpty())
            dataManager.commit(new CommitContext(toCommit));
    }

    private boolean isEditable() {
        return isNewItem(getItem())
                || (!relationTypeIdsFromDb.contains(getItem().getId())
                && BooleanUtils.isFalse(existRelatedCardsByType(getItem())));
    }

    private boolean isNewItem(T item) {
        return PersistenceHelper.isNew(item);
    }

    private boolean existRelatedCardsByType(RelationTypeMPA relationTypeMPA) {
        LoadContext<RelatedCards> loadContext = new LoadContext<>(RelatedCards.class);
        // language=JPAQL
        String query = "select rc from bratsk$RelatedCards rc " +
                "           where rc.sourceRelationType.id = :relationTypeId " +
                "           or rc.relatedRelationType.id = :relationTypeId ";
        loadContext
                .setQueryString(query)
                .setParameter("relationTypeId", relationTypeMPA.getId());
        return getDsContext().getDataSupplier().getCount(loadContext) > 0;
    }

}