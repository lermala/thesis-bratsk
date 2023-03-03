/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.core.app.reassignment.commands;

import com.haulmont.thesis.core.app.reassignment.commands.AbstractDocReassignmentCommand;
import com.company.bratsk.entity.Mpa;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component(MpaReassignmentCommand.NAME)
public class MpaReassignmentCommand extends AbstractDocReassignmentCommand<Mpa> {
    protected static final String NAME = "bratsk_MpaReassignmentCommand";

    @PostConstruct
    protected void postInit() {
        type = "Mpa";
        docQuery = String.format(DOC_QUERY_TEMPLATE, "bratsk$Mpa");
    }

    @Override
    public String getName() {
        return NAME;
    }
}