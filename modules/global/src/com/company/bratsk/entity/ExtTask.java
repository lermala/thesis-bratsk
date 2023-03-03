/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.entity;

import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.thesis.core.entity.Task;

import javax.persistence.*;

/**
 * @author IGoridko
 */
@Entity(name = "bratsk$ExtTask")
@Extends(Task.class)
@Table(name = "TM_TASK")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "ID")
@DiscriminatorValue("200")
@Listeners("bratsk_TaskListener")
public class ExtTask extends Task {

    private static final long serialVersionUID = 5713713406855659868L;

    @Column(name = "SUBTASKS_FINISHED")
    protected Boolean subtasksFinished = false;

    public Boolean getSubtasksFinished() {
        return subtasksFinished;
    }

    public void setSubtasksFinished(Boolean subtasksFinished) {
        this.subtasksFinished = subtasksFinished;
    }

}
