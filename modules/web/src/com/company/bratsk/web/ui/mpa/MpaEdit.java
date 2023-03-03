/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.mpa;

import com.company.bratsk.entity.BratskConstants;
import com.company.bratsk.entity.RelatedCards;
import com.company.bratsk.entity.RelationTypeMPA;
import com.company.bratsk.web.ui.common.links.RelatedCardsFrame;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.thesis.web.actions.PrintReportAction;
import com.haulmont.thesis.web.ui.basicdoc.editor.AbstractDocEditor;
import com.haulmont.thesis.web.voice.VoiceActionPriorities;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.thesis.core.entity.DocCategory;
import com.company.bratsk.entity.Mpa;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static com.haulmont.thesis.web.voice.VoiceCompanionsRepository.voiceCompanion;

public class MpaEdit<T extends Mpa> extends AbstractDocEditor<T> {

    @Inject
    protected LookupPickerField<DocCategory> docCategory;
    @Inject
    protected TextField<RelationTypeMPA> mpaStatus;
    @Named("cardRelatesFrame")
    protected RelatedCardsFrame relatedCardsFrame;
    @Inject
    protected LookupPickerField executorUser;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initVoiceControl();
    }

    @Override
    public void ready() {
        super.ready();
        setProjectStatusIfNeeded();
    }

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        cardRelatesFrameTabConfig();
        if (PersistenceHelper.isNew(item) && !BooleanUtils.isTrue(((Mpa)item).getTemplate())) {
            executorUser.setValue(userSession.getCurrentOrSubstitutedUser());
        }
        executorUser.setEnabled(userSessionTools.isCurrentUserAdministrator() || userSessionTools.isCurrentUserInRole("doc_secretary"));
    }

    @Override
    protected void applyAccessData() {
        super.applyAccessData();
        executorUser.setEnabled(userSessionTools.isCurrentUserAdministrator() || userSessionTools.isCurrentUserInRole("doc_secretary"));
    }

    private void setProjectStatusIfNeeded() {
        if (isNewItem(getItem())) {
            LoadContext<RelationTypeMPA> projectStatus = new LoadContext<>(RelationTypeMPA.class);
            projectStatus.setQueryString("select rt from bratsk$RelationTypeMPA rt where rt.id = :id")
                    .setParameter("id", BratskConstants.StatusNameConstants.PROJECT_ID);
            RelationTypeMPA initialStatus = getDsContext().getDataSupplier().load(projectStatus);
            getItem().setMpaStatus(initialStatus);
        }
    }

    public void cardRelatesFrameTabConfig() {
        if (!getItem().getTemplate()) {
            addCounterOnTab((CollectionDatasource<RelatedCards, UUID>) relatedCardsFrame.getDsContext()
                    .get("cardRelatesDs"), "cardRelatesFrameTab");
            ((CollectionDatasource<RelatedCards, UUID>) relatedCardsFrame.getDsContext()
                    .get("cardRelatesDs")).refresh();
        } else {
            removeTabIfExist(tabsheet, "cardRelatesFrameTab");
        }
    }

    @Override
    protected String getHiddenTabsConfig() {
        return "processTab,openHistoryTab,securityTab,cardProjectsTab,correspondenceHistoryTab," +
                "docTransferLogTab,cardLinksTab,docLogTab,versionsTab";
    }

    @Override
    protected void addPrintDocActions() {
        super.addPrintDocActions();
        printButton.addAction(new PrintReportAction("printExecutionList", this,
                "printDocExecutionListReportName"));
    }

    protected void initVoiceControl() {
        voiceCompanion(docCategory).setPriorityOffset(VoiceActionPriorities.TAB_SHEET + 10);
    }

    @Override
    protected void fillHiddenTabs() {
        hiddenTabs.put("office", getMessage("office"));
        hiddenTabs.put("attachmentsTab", getMessage("attachmentsTab"));
        hiddenTabs.put("docTreeTab", getMessage("docTreeTab"));
        if (getAccessData().getNotVersion()) {
            hiddenTabs.put("cardCommentTab", getMessage("cardCommentTab"));
        }
        super.fillHiddenTabs();
    }

    @Override
    protected void postInit() {
        super.postInit();
        executorUser.setEnabled(userSessionTools.isCurrentUserAdministrator() || userSessionTools.isCurrentUserInRole("doc_secretary"));
    }
}