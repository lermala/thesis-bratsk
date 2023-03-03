/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.EnableRestore;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;
import com.haulmont.cuba.core.global.DeletePolicy;
import org.apache.commons.lang.BooleanUtils;

import javax.persistence.*;

@Table(name = "BRATSK_RELATION_TYPE_MPA")
@Entity(name = "bratsk$RelationTypeMPA")
@EnableRestore
@TrackEditScreenHistory
@NamePattern("%s|typeName")
public class RelationTypeMPA extends StandardEntity {
    private static final long serialVersionUID = 2046489991449122986L;

    @Column(name = "TYPE_NAME", length = 60, unique = true)
    protected String typeName;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "REVERSE_NAME_ID")
    protected RelationTypeMPA reverseName;

    @Column(name = "COMMENT_")
    protected String comment;

    @Column(name = "IS_AN_ACTION")
    protected Boolean isAnAction = false;

    @Column(name = "IS_SYSTEM")
    protected Boolean isSystem = false;

    public Boolean getIsSystem() {
        return BooleanUtils.isTrue(this.isSystem);
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Boolean getIsAnAction() {
        return isAnAction;
    }

    public void setIsAnAction(Boolean isAnAction) {
        this.isAnAction = isAnAction;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public RelationTypeMPA getReverseName() {
        return reverseName;
    }

    public void setReverseName(RelationTypeMPA reverseName) {
        this.reverseName = reverseName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}