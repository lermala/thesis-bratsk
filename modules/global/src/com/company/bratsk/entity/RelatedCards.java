/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.entity;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.thesis.core.entity.TsCard;

import javax.persistence.*;

@Table(name = "BRATSK_RELATED_CARDS")
@Entity(name = "bratsk$RelatedCards")
public class RelatedCards extends StandardEntity {
    private static final long serialVersionUID = 7006195910556380845L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_CARD_ID")
    protected TsCard sourceCard;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RELATED_CARD_ID")
    protected TsCard relatedCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_RELATION_TYPE_ID")
    protected RelationTypeMPA sourceRelationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RELATED_RELATION_TYPE_ID")
    protected RelationTypeMPA relatedRelationType;

    public RelationTypeMPA getRelatedRelationType() {
        return relatedRelationType;
    }

    public void setRelatedRelationType(RelationTypeMPA relatedRelationType) {
        this.relatedRelationType = relatedRelationType;
    }

    public RelationTypeMPA getSourceRelationType() {
        return sourceRelationType;
    }

    public void setSourceRelationType(RelationTypeMPA sourceRelationType) {
        this.sourceRelationType = sourceRelationType;
    }

    public TsCard getRelatedCard() {
        return relatedCard;
    }

    public void setRelatedCard(TsCard relatedCard) {
        this.relatedCard = relatedCard;
    }

    public TsCard getSourceCard() {
        return sourceCard;
    }

    public void setSourceCard(TsCard sourceCard) {
        this.sourceCard = sourceCard;
    }
}
