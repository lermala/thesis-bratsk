/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.themarubric;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.bratsk.entity.ThemaRubric;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.thesis.core.global.TsPersistenceHelper;
import com.haulmont.thesis.web.ui.basic.params.ThesisScreenParameters;

import javax.inject.Inject;
import java.util.Map;


public class ThemaRubricEdit<T extends ThemaRubric> extends AbstractEditor<T> {
    @Inject
    protected PickerField parentThemaRubric;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initDialogOptions();
        initDep(params);
    }

    protected void initDialogOptions() {
        getDialogOptions().setWidthAuto().setHeightAuto();
    }

    private void initDep(final Map<String, Object> params) {
        ThemaRubric item = (ThemaRubric) params.get("ITEM");
        parentThemaRubric.removeAction(parentThemaRubric.getAction(PickerField.LookupAction.NAME));
        parentThemaRubric.removeAction(parentThemaRubric.getAction(PickerField.ClearAction.NAME));
        final PickerField.LookupAction parentThemaRubricLookupAction = new PickerField.LookupAction(parentThemaRubric) {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);

            }
        };
        parentThemaRubricLookupAction.setLookupScreen("bratsk$ThemaRubric.lookup");
        parentThemaRubricLookupAction.setLookupScreenParams(params);
        parentThemaRubric.addAction(parentThemaRubricLookupAction);

//        if (!params.containsKey("visibleOrganizationField")) {
//            params.put("visibleOrganizationField", false);
//        }

//        if (!ThesisScreenParameters.ORGANIZATION.isPresent(params) && TsPersistenceHelper.isPropertyAccessible(item, "organization") && item.getOrganization() != null) {
//            ThesisScreenParameters.ORGANIZATION.set(params, item.getOrganization());
//        }
//
//        if (!ThesisScreenParameters.DEPARTMENT.isPresent(params)) {
//            ThesisScreenParameters.DEPARTMENT.set(params, item);
//        }
    }
}