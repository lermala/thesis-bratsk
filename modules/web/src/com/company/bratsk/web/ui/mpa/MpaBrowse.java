/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.mpa;

import com.company.bratsk.entity.BratskConstants;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.thesis.web.ui.basicdoc.browse.AbstractDocBrowser;
import com.company.bratsk.entity.Mpa;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

public class MpaBrowse<T extends Mpa> extends AbstractDocBrowser<T> {

    @Inject
    protected Table<Mpa> cardsTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        entityName = "bratsk$Mpa";
        setColorForTable();
    }

    private void setColorForTable() {
        cardsTable.addStyleProvider(new Table.StyleProvider<Mpa>() {
            @Nullable
            @Override
            public String getStyleName(Mpa entity, @Nullable String property) {
                if (property == null) {
                    return null;
                } else {
                    if (BratskConstants.StatusNameConstants.ACTIVE_ID.equals(entity.getMpaStatus().getId())
                            || BratskConstants.StatusNameConstants.ACTIVE_WITH_MODIFICATIONS
                            .equals(entity.getMpaStatus().getId())) {
                        return "green";
                    } else if (BratskConstants.StatusNameConstants.ACTION_STOPPED
                            .equals(entity.getMpaStatus().getId())) {
                        return "purple";
                    } else if (BratskConstants.StatusNameConstants.CANCELED.equals(entity.getMpaStatus().getId())
                            || BratskConstants.StatusNameConstants.EXPIRED.equals(entity.getMpaStatus().getId())) {
                        return "red";
                    }
                }
                return null;
            }
        });
    }
}