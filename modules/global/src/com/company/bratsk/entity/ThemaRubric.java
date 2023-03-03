/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.EnableRestore;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;

import javax.persistence.*;

@Table(name = "BRATSK_THEMA_RUBRIC")
@Entity(name = "bratsk$ThemaRubric")
@EnableRestore
@TrackEditScreenHistory
@NamePattern("%s|name")
public class ThemaRubric extends StandardEntity {

    private static final long serialVersionUID = -4991814038039694388L;
    @Column(name = "NAME", length = 500)
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_THEMA_RUBRIC_ID")
    protected ThemaRubric parentThemaRubric;

    public ThemaRubric getParentThemaRubric() {
        return parentThemaRubric;
    }

    public void setParentThemaRubric(ThemaRubric parentThemaRubric) {
        this.parentThemaRubric = parentThemaRubric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}