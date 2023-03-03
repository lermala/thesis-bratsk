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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "BRATSK_STATUS_NORMATIV")
@Entity(name = "bratsk$StatusNormativ")
@EnableRestore
@TrackEditScreenHistory
@NamePattern("%s|name")
public class StatusNormativ extends StandardEntity {

    private static final long serialVersionUID = 6343330149761702488L;
    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;
    @Column(name = "NAME", nullable = false, length = 50)
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}