/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.common.links;

import com.company.bratsk.entity.Mpa;
import com.company.bratsk.entity.RelatedCards;
import com.company.bratsk.entity.RelationTypeMPA;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.thesis.core.entity.TsCard;
import com.haulmont.thesis.web.gui.components.ThesisWebTable;
import com.haulmont.thesis.web.ui.tools.DocumentTools;
import com.haulmont.thesis.web.ui.tools.ImportantCardTools;

import javax.inject.Inject;
import java.util.Map;

public class CardsToRelateController extends AbstractWindow {

    @Inject
    protected Table<Mpa> mpaTable;
    @Inject
    protected LookupField<RelationTypeMPA> sourceRelationType;
    @Inject
    protected LookupField<RelationTypeMPA> relatedRelationType;
    @Inject
    protected Metadata metadata;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected ImportantCardTools importantCardTools;
    @Inject
    protected DocumentTools documentTools;
    @Inject
    protected Configuration configuration;

    protected TsCard sourceCard;
    protected boolean isListenerWorking = false;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        sourceCard = (TsCard) params.get("card");
        addColumns();
        initActions();
        initRelationTypeListeners();
    }

    protected void onWindowCommit() {
        if (mpaTable != null && !mpaTable.getSelected().isEmpty()) {
            CommitContext commitContext = new CommitContext();
            if (sourceRelationType.getValue() != null && relatedRelationType.getValue() != null) {
                for (Entity entity : mpaTable.getSelected()) {
                    RelatedCards relatedCard = createRelatedCards((TsCard) entity);
                    ((Mpa) relatedCard.getRelatedCard()).setMpaStatus(getStatus());
                    commitContext.getCommitInstances().add(relatedCard.getRelatedCard());
                    commitContext.getCommitInstances().add(relatedCard);
                }
                getDsContext().getDataSupplier().commit(commitContext);
                this.close(Window.COMMIT_ACTION_ID, true);
            } else {
                showNotification(messages.getMessage(getClass(),
                        "notValid"), NotificationType.WARNING);
            }
        } else {
            showNotification(messages.getMessage(getClass(), "notSelectedMpa"), NotificationType.WARNING);
        }
    }

    private RelationTypeMPA getStatus() {
        LoadContext<RelationTypeMPA> status = new LoadContext<>(RelationTypeMPA.class);
        status.setQueryString("select rt from bratsk$RelationTypeMPA rt where rt.isAnAction = true" +
                " and rt.id = :id")
                .setParameter("id", sourceRelationType.getValue().getId());
        status.setView("relationType-edit");
        RelationTypeMPA relationTypeMPA = getDsContext().getDataSupplier().load(status);
        return relationTypeMPA.getReverseName();
    }

    protected RelatedCards createRelatedCards(TsCard entity) {
        RelatedCards relatedCard = metadata.create(RelatedCards.class);
        relatedCard.setSourceCard(sourceCard);
        relatedCard.setRelatedCard(entity);
        relatedCard.setSourceRelationType(sourceRelationType.getValue());
        relatedCard.setRelatedRelationType(relatedRelationType.getValue());
        relatedCard.setCreatedBy(userSessionSource.getUserSession().getCurrentOrSubstitutedUser().getLogin());

        return relatedCard;
    }

    protected void addColumns() {
        importantCardTools.addImportantColumn(mpaTable);
        documentTools.addAttachmentsColumn(mpaTable);
    }

    protected void initActions() {
        ClientConfig clientConfig = configuration.getConfig(ClientConfig.class);
        addAction(new AbstractAction("windowCommit", clientConfig.getCommitShortcut()) {
            public void actionPerform(Component component) {
                onWindowCommit();
            }

            @Override
            public String getCaption() {
                return messages.getMessage(AppConfig.getMessagesPack(), "actions.Ok");
            }
        });

        addAction(new AbstractAction("windowClose", clientConfig.getCloseShortcut()) {
            public void actionPerform(Component component) {
                onWindowClose();
            }

            @Override
            public String getCaption() {
                return messages.getMessage(AppConfig.getMessagesPack(), "actions.Cancel");
            }
        });
        mpaTable.setItemClickAction(new OpenAction());
    }

    protected void initRelationTypeListeners() {
        sourceRelationType.addValueChangeListener(event -> {
            if (!isListenerWorking) {
                if (event.getValue() != null) {
                    isListenerWorking = true;
                    relatedRelationType.setValue(event.getValue().getReverseName());
                }
                isListenerWorking = false;
            }
        });
        relatedRelationType.addValueChangeListener(event -> {
            if (!isListenerWorking) {
                if (event.getValue() != null) {
                    isListenerWorking = true;
                    sourceRelationType.setValue(event.getValue().getReverseName());
                }
                isListenerWorking = false;
            }
        });
    }

    protected void onWindowClose() {
        this.close(Window.CLOSE_ACTION_ID, true);
    }

    protected class OpenAction extends AbstractAction {

        protected OpenAction() {
            super("open");
        }

        @Override
        public void actionPerform(Component component) {
            String windowAlias = "";
            TsCard openCard = (TsCard) ((ThesisWebTable) component).getSingleSelected();
            if (openCard != null) {
                windowAlias = openCard.getMetaClass().getName() + ".edit";
            }
            openEditor(windowAlias, openCard, WindowManager.OpenType.THIS_TAB);

        }
    }
}
