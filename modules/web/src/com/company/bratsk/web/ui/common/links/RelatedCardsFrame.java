/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package com.company.bratsk.web.ui.common.links;

import com.company.bratsk.entity.BratskConstants;
import com.company.bratsk.entity.Mpa;
import com.company.bratsk.entity.RelatedCards;
import com.company.bratsk.entity.RelationTypeMPA;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.ListActionType;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.thesis.core.entity.TsCard;
import com.haulmont.thesis.gui.ThesisWebComponentsFactory;
import com.haulmont.workflow.core.entity.Card;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

public class RelatedCardsFrame extends AbstractFrame {

    @Inject
    protected Button addRelate;
    @Inject
    protected Datasource<Card> cardDs;
    @Inject
    protected CollectionDatasource<RelatedCards, UUID> cardRelatesDs;
    @Inject
    protected Table<RelatedCards> relatesTable;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ThesisWebComponentsFactory componentsFactory;
    @Inject
    protected LookupField<TsCard> currentDoc;
    @Inject
    protected LookupField<RelationTypeMPA> sourceRelationType;
    @Inject
    protected LookupField<RelationTypeMPA> relatedRelationType;
    @Inject
    protected LookupField<TsCard> relatedDoc;
    @Inject
    protected Notifications notifications;
    protected RelationTypeMPA lastStatus;
    protected Card card;
    protected Mpa mpa;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        card = setCard(params);
        if (card == null) {
            card = setCardIfStillNull(params);
        }
        params.put("cardId", card.getId());

        cardRelatesDs.refresh(params);
        initAddButton();
        initDsListener();
        initActions();
        refreshCardRelates();
        getTableInput();
    }

    protected void getTableInput() {
        relatesTable.addGeneratedColumn("related", new Table.ColumnGenerator<RelatedCards>() {
            @Nullable
            @Override
            public Component generateCell(RelatedCards entity) {
                Label component = componentsFactory.createComponent(Label.class);
                Optional<RelatedCards> any = cardRelatesDs.getItems().stream().filter(cr -> cr.getRelatedCard()
                        .getId().equals(card.getId())
                        && entity.getSourceCard().getId().equals(cr.getSourceCard().getId())).findAny();
                if (any.isPresent()) {
                    component.setValue(entity.getSourceCard().getDescription());
                } else {
                    Optional<RelatedCards> another = cardRelatesDs.getItems().stream().filter(cr -> cr.getSourceCard()
                            .getId().equals(card.getId())
                            && entity.getRelatedCard().getId().equals(cr.getRelatedCard().getId())).findAny();
                    if (another.isPresent()) {
                        component.setValue(entity.getRelatedCard().getDescription());
                    }
                }
                return component;
            }
        });
    }


    public void refreshCardRelates() {
        if (((CollectionDatasourceImpl) cardRelatesDs).getRefreshMode()
                .equals(CollectionDatasource.RefreshMode.NEVER)) {
            ((CollectionDatasourceImpl) cardRelatesDs).setRefreshMode(CollectionDatasource.RefreshMode.ALWAYS);
        }
        cardRelatesDs.refresh();
    }

    protected void initActions() {
        relatesTable.setItemClickAction(createOpenAction());

        initRemoveButton();
    }

    protected void initRemoveButton() {
        RemoveAction removeAction = (RemoveAction) relatesTable.getAction(ListActionType.REMOVE.getId());
        if (removeAction != null) {
            removeAction.setAfterRemoveHandler(removedItems -> {
                for (Object removedItem : removedItems) {
                    if (removedItem instanceof RelatedCards) {
                        RelatedCards removed = (RelatedCards) removedItem;
                            statusChangeEvent(removed.getRelatedCard(), removed.getRelatedRelationType().getId(),
                                    isCurrentCardSource(removed));
                    }
                }
            });
        }
    }

    private void statusChangeEvent(TsCard tsCard, UUID deletedStatusId, boolean isChangeNotForCurrentCard) {
        lastStatus = getLastStatus(tsCard);
        mpa = isChangeNotForCurrentCard ? getDsContext().getDataSupplier().reload(((Mpa) tsCard),
                "for-status-change") : ((Mpa) cardDs.getItem());
        if (mpa.getMpaStatus().getId().equals(deletedStatusId)) {
            if (lastStatus != null && !lastStatus.getIsAnAction()) {
                mpa.setMpaStatus(lastStatus);
            } else {
                mpa.setMpaStatus(mpa.getRegistered() ? getStatus(BratskConstants.StatusNameConstants.ACTIVE_ID)
                        : getStatus(BratskConstants.StatusNameConstants.PROJECT_ID));
            }
            if (isChangeNotForCurrentCard) {
                getDsContext().getDataSupplier().commit(mpa);
            }
            showNotificationAfterStatusChange(!isChangeNotForCurrentCard);
        }
    }

    private void showNotificationAfterStatusChange(boolean isChangeNotForCurrentCard) {
        if (isChangeNotForCurrentCard) {
            cardDs.commit();
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(getMessage("relatedCardsFrame.attention"))
                    .withDescription(getMessage("relatedCardsFrame.saveNotification"))
                    .show();
        }
    }

    private RelationTypeMPA getStatus(UUID uuid) {
        LoadContext<RelationTypeMPA> status = new LoadContext<>(RelationTypeMPA.class);
        status.setQueryString("select rt from bratsk$RelationTypeMPA rt " +
                "where rt.id = :id")
                .setParameter("id", uuid);
        return getDsContext().getDataSupplier().load(status);
    }

    private RelationTypeMPA getLastStatus(TsCard tsCard) {
        LoadContext<RelationTypeMPA> lastStatus = new LoadContext<>(RelationTypeMPA.class);
        lastStatus.setQueryString("select rc.relatedRelationType from bratsk$RelatedCards rc where " +
                "rc.deleteTs is null and rc.relatedCard.id = :id" +
                " order by rc.createTs desc")
                .setParameter("id", tsCard.getId());
        lastStatus.setView("relationType-edit");
        return getDsContext().getDataSupplier().load(lastStatus);
    }

    public Card setCard(Map<String, Object> params) {
        if (cardDs == null || cardDs.getItem() == null) {
            return (Card) params.get("card");
        } else {
            return cardDs.getItem();
        }
    }

    public Card setCardIfStillNull(Map<String, Object> params) {
        if (cardDs == null || cardDs.getItem() == null) {
            return (Card) params.get("ITEM");
        } else {
            return cardDs.getItem();
        }
    }

    protected void initAddButton() {
        addRelate.setAction(new CreateAction(relatesTable) {
            @Override
            public void actionPerform(Component component) {
                if (PersistenceHelper.isNew(cardDs.getItem())) {
                    frame.getFrame().showOptionDialog(
                            messages.getMessage(this.getClass(), "saveCard.caption"),
                            messages.getMessage(this.getClass(), "saveRelates.caption"),
                            Frame.MessageType.CONFIRMATION,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES, true) {
                                        @Override
                                        public void actionPerform(Component component) {
                                            if (((Window.Editor) frame.getFrame().getFrameOwner()).commit()) {
                                                openCardLookup();
                                            }
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.NO) {
                                    }
                            });
                } else {
                    openCardLookup();
                }
            }


            @Override
            public Map<String, Object> getWindowParams() {
                Map<String, Object> innerParams = super.getWindowParams();
                if (innerParams == null) innerParams = new HashMap<>();
                innerParams.put("card", cardDs.getItem());
                return innerParams;
            }
        });
    }

    protected void openCardLookup() {
        Set<TsCard> relatedAndSourceCards = new HashSet<>();
        Action action = addRelate.getAction();
        Map<String, Object> params = new HashMap<>();

        for (RelatedCards relatedCards : cardRelatesDs.getItems()) {
            relatedAndSourceCards.add(relatedCards.getRelatedCard());
            relatedAndSourceCards.add(relatedCards.getSourceCard());
        }

        if (action instanceof CreateAction) {
            CreateAction createAction = (CreateAction) action;
            params.putAll(createAction.getWindowParams());
        }

        params.put("relatedCards", relatedAndSourceCards);
        params.put("exclItem", cardDs.getItem());

        Window window = openWindow("cardsToRelate", WindowManager.OpenType.THIS_TAB, params);
        window.addListener(actionId -> {
            cardRelatesDs.refresh();
        });
    }

    protected RelatedCardsFrameOpenAction createOpenAction() {
        return new RelatedCardsFrameOpenAction();
    }

    protected class RelatedCardsFrameOpenAction extends AbstractAction {
        public RelatedCardsFrameOpenAction() {
            super();
        }

        @Override
        public void actionPerform(Component component) {
            if (cardDs.getItem() == null || cardRelatesDs.getItem() == null) {
                return;
            }

            TsCard openCard;
            if (cardDs.getItem().equals(cardRelatesDs.getItem().getRelatedCard())) {
                openCard = cardRelatesDs.getItem().getSourceCard();
            } else {
                openCard = cardRelatesDs.getItem().getRelatedCard();
            }
            Map<String, Object> params = new HashMap<>();
            MetaClass metaClass = metadata.getClass(openCard.getClass());
            if (metadata.getExtendedEntities().getOriginalMetaClass(metaClass) != null)
                metaClass = metadata.getExtendedEntities().getOriginalMetaClass(metaClass);
            openEditor(metaClass.getName() + ".edit", openCard, WindowManager.OpenType.THIS_TAB, params);
        }
    }

    protected void initDsListener() {
        cardRelatesDs.addItemChangeListener(itemChange -> {
            if (itemChange.getItem() != null) {
                setCardRelateItemValues(itemChange.getItem());
            } else {
                clearRelatedCardInfo();
            }
        });
    }

    protected boolean isCurrentCardSource(RelatedCards item) {
        return card.getId().equals(item.getSourceCard().getId());
    }

    protected boolean isCurrentCardRelated(RelatedCards item) {
        return card.getId().equals(item.getRelatedCard().getId());
    }

    public void setCardRelateItemValues(RelatedCards relatedCards) {
        if (isCurrentCardSource(relatedCards)) {
            currentDoc.setValue(relatedCards.getSourceCard());
            sourceRelationType.setValue(relatedCards.getSourceRelationType());
            relatedRelationType.setValue(relatedCards.getRelatedRelationType());
            relatedDoc.setValue(relatedCards.getRelatedCard());
        }
        if (isCurrentCardRelated(relatedCards)) {
            currentDoc.setValue(relatedCards.getRelatedCard());
            sourceRelationType.setValue(relatedCards.getRelatedRelationType());
            relatedRelationType.setValue(relatedCards.getSourceRelationType());
            relatedDoc.setValue(relatedCards.getSourceCard());
        }
        ((CollectionDatasourceImpl<RelatedCards, UUID>) cardRelatesDs).setModified(false);
    }

    protected void clearRelatedCardInfo() {
        currentDoc.clear();
        sourceRelationType.clear();
        relatedRelationType.clear();
        relatedDoc.clear();
    }
}

