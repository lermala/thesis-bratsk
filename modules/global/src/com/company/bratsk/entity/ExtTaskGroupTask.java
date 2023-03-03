/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.entity;

import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.thesis.core.entity.TaskGroupTask;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "bratsk$ExtTaskGroupTask")
@Extends(TaskGroupTask.class)
public class ExtTaskGroupTask extends TaskGroupTask {
    private static final long serialVersionUID = 5186423583112309722L;

    @Column(name = "RESPONSIBLE_EXECUTOR")
    protected Boolean responsibleExecutor = false;

    public Boolean getResponsibleExecutor() {
        return responsibleExecutor;
    }

    public void setResponsibleExecutor(Boolean responsibleExecutor) {
        this.responsibleExecutor = responsibleExecutor;
    }
}