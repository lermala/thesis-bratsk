/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.taskpattern;

import com.haulmont.thesis.core.entity.TaskPattern;
import com.haulmont.thesis_holding.web.ui.taskpattern.HoldingTaskPatternEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author IGoridko
 */
public class ExtHoldingTaskPatternEditor<T extends TaskPattern> extends HoldingTaskPatternEditor<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtHoldingTaskPatternEditor.class);

    @Override
    public void ready() {
        super.ready();
        configureConfirmRequiredCheckBox();
    }

    private void configureConfirmRequiredCheckBox() {
        confirmRequired.setVisible(false);
    }

}
