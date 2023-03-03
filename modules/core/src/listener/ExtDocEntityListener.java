/*
 * Copyright (c) 2019 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */
package listener;

import com.company.bratsk.entity.BratskConstants;
import com.company.bratsk.entity.Mpa;
import com.company.bratsk.entity.RelationTypeMPA;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.thesis.core.app.CardSecurityAPI;
import com.haulmont.thesis.core.app.ReservationNumberService;
import com.haulmont.thesis.core.entity.*;
import com.haulmont.thesis.core.enums.DocOfficeDocKind;
import com.haulmont.thesis.core.enums.ReservNumberState;
import com.haulmont.thesis.core.global.TsPersistenceHelper;
import com.haulmont.thesis.core.listener.DocEntityListener;
import com.haulmont.thesis.core.sys.CardAclManagerAPI;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;


public class ExtDocEntityListener extends DocEntityListener {

    @Override
    public void onBeforeUpdate(Doc entity, EntityManager em) {
        if (checkMpaStatusForChanging(entity)) {
            ((Mpa) entity).setMpaStatus(getActiveStatusAfterRegistration());
        }
        super.onBeforeUpdate(entity, em);
    }

    private RelationTypeMPA getActiveStatusAfterRegistration() {
        return persistence.getEntityManager().createQuery("select rt from bratsk$RelationTypeMPA rt " +
                "where rt.id = :id", RelationTypeMPA.class)
                .setParameter("id", BratskConstants.StatusNameConstants.ACTIVE_ID).getFirstResult();
    }

    private boolean checkMpaStatusForChanging(Doc entity){
        return entity instanceof Mpa
                && ((Mpa) entity).getMpaStatus() != null
                && BratskConstants.StatusNameConstants.PROJECT_ID.equals(((Mpa) entity).getMpaStatus().getId())
                && entity.getRegistered();
    }
}
