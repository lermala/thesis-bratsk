/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.entity;

import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.annotation.EnableRestore;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.TrackEditScreenHistory;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.HasDetailedDescription;
import com.haulmont.thesis.core.entity.TsUser;
import com.haulmont.thesis.core.global.EntityCopyUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

@DiscriminatorValue("2000")
@Table(name = "BRATSK_MPA")
@Entity(name = "bratsk$Mpa")
@Listeners("thesis_DocEntityListener")
@EnableRestore
@TrackEditScreenHistory
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "ID")
public class Mpa extends Doc implements HasDetailedDescription {

    private static final long serialVersionUID = 5463729779704842680L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVER_USER_ID")
    protected TsUser approverUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RELATION_TYPE_MPA_STATUS_ID")
    protected RelationTypeMPA mpaStatus;

    @Column(name = "COUNT_LIST")
    protected Integer countList;

    @Column(name = "IS_PUBLIC")
    protected Boolean isPublic = false;

    @Column(name = "DATE_PUBLIC_SITE")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date datePublicSite;

    @Column(name = "IS_PUBLIC_SITE")
    protected Boolean isPublicSite;

    @JoinColumn(name = "THEMATIC_PUBRIC_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(DeletePolicy.CASCADE)
    protected ThemaRubric thematicPubric;

    @Column(name = "SENT_TO_PROCURATURA")
    protected Boolean sentToProcuratura;

    @Column(name = "DATE_SENT_TO_PROCURATURE")
    @Temporal(TemporalType.DATE)
    protected Date dateSentToProcurature;

    @JoinColumn(name = "PREPARED_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    protected TsUser prepared;

    @JoinColumn(name = "EXECUTOR_USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(DeletePolicy.CASCADE)
    protected TsUser executorUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_NORMATIV_ID")
    protected StatusNormativ statusNormativ;

    public StatusNormativ getStatusNormativ() {
        return statusNormativ;
    }

    public void setStatusNormativ(StatusNormativ statusNormativ) {
        this.statusNormativ = statusNormativ;
    }

    public TsUser getExecutorUser() {
        return executorUser;
    }

    public void setExecutorUser(TsUser executorUser) {
        this.executorUser = executorUser;
    }

    public TsUser getPrepared() {
        return prepared;
    }

    public void setPrepared(TsUser prepared) {
        this.prepared = prepared;
    }

    public Date getDateSentToProcurature() {
        return dateSentToProcurature;
    }

    public void setDateSentToProcurature(Date dateSentToProcurature) {
        this.dateSentToProcurature = dateSentToProcurature;
    }

    public Boolean getSentToProcuratura() {
        return sentToProcuratura;
    }

    public void setSentToProcuratura(Boolean sentToProcuratura) {
        this.sentToProcuratura = sentToProcuratura;
    }

    public ThemaRubric getThematicPubric() {
        return thematicPubric;
    }

    public void setThematicPubric(ThemaRubric thematicPubric) {
        this.thematicPubric = thematicPubric;
    }

    public Boolean getIsPublicSite() {
        return isPublicSite;
    }

    public void setIsPublicSite(Boolean isPublicSite) {
        this.isPublicSite = isPublicSite;
    }

    public Date getDatePublicSite() {
        return datePublicSite;
    }

    public void setDatePublicSite(Date datePublicSite) {
        this.datePublicSite = datePublicSite;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Integer getCountList() {
        return countList;
    }

    public void setCountList(Integer countList) {
        this.countList = countList;
    }

    public TsUser getApproverUser() {
        return approverUser;
    }

    public void setApproverUser(TsUser approverUser) {
        this.approverUser = approverUser;
    }

    public RelationTypeMPA getMpaStatus() {
        return mpaStatus;
    }

    public void setMpaStatus(RelationTypeMPA mpaStatus) {
        this.mpaStatus = mpaStatus;
    }

    @Override
    public void copyFrom(Doc srcDoc, Set<com.haulmont.cuba.core.entity.Entity> toCommit, boolean copySignatures,
                         boolean onlyLastAttachmentsVersion, boolean useOriginalAttachmentCreatorAndCreateTs,
                         boolean copyAllVersionMainAttachment) {
        super.copyFrom(srcDoc, toCommit, copySignatures, onlyLastAttachmentsVersion,
                useOriginalAttachmentCreatorAndCreateTs, copyAllVersionMainAttachment);
        Metadata metadata = AppBeans.get(Metadata.NAME);
        MetaClass metaClass = metadata.getClassNN(getClass());
        EntityCopyUtils.copyProperties(srcDoc, this, metaClass.getOwnProperties(), toCommit);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String getDetailedDescription(Locale locale, boolean includeState, boolean includeShortInfo) {
        Messages messages = AppBeans.get(Messages.NAME);
        String dateFormat = Datatypes.getFormatStrings(locale).getDateFormat();
        String description;
        String locState = includeState ? getLocState(locale) : StringUtils.EMPTY;

        description = getDocKind().getName() + " "
                + (StringUtils.isBlank(getNumber()) ? "" : messages.getMessage(Doc.class, "notification.number", locale) + " " + getNumber() + " ")
                + (getDate() == null ? "" : messages.getMessage(Doc.class, "notification.from", locale) + " "
                + new SimpleDateFormat(dateFormat).format(getDate()))
                + (includeState && StringUtils.isNotBlank(locState) ? " [" + locState + "]" : "");


        if (includeShortInfo && (StringUtils.isNotBlank(getTheme()) || StringUtils.isNotBlank(getComment()))) {
            int length = description.length();
            int infoLength = MAX_SUBJECT_LENGTH - length;

            if (infoLength < MIN_SHORT_INFO_LENGTH) return description;

            String shortInfo = StringUtils.defaultIfBlank(getTheme(), getComment());
            return description + " - " + StringUtils.abbreviate(shortInfo, infoLength);
        } else {
            return description;
        }
    }
}