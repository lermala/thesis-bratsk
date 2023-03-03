/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package process.mpaEndorsement

import com.haulmont.cuba.core.EntityManager
import com.haulmont.cuba.core.Persistence
import com.haulmont.cuba.core.Query
import com.haulmont.cuba.core.Transaction
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Configuration
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.UserSessionSource
import com.haulmont.cuba.security.entity.User
import com.haulmont.thesis.core.config.ThesisConfig
import com.haulmont.thesis.core.entity.Contract
import com.haulmont.thesis.core.entity.Doc
import com.haulmont.thesis.core.entity.Employee
import com.haulmont.thesis.core.notification.NotificationUtils
import com.haulmont.workflow.core.app.SmsSenderConfig
import com.haulmont.workflow.core.entity.Card

//import com.haulmont.workflow.core.entity.Assignment
//import com.haulmont.workflow.core.entity.CardRole

Doc c = card;
User u = user;
//Assignment a = assignment;
//CardRole cr = cardRole;

String lang = u.language ? u.language : AppBeans.get(UserSessionSource.class).getUserSession().getLocale().getLanguage();
User currentUser = AppBeans.get(UserSessionSource.class).getUserSession().getCurrentOrSubstitutedUser();

String phone = "";
StringBuilder currentUserName = new StringBuilder();
if (u) {
    Transaction tx =  AppBeans.get(Persistence.class).getTransaction();
    try {
        EntityManager em =  AppBeans.get(Persistence.class).getEntityManager();
        Query q = em.createQuery("select e from df\$Employee e where e.user.id = :user").setParameter("user", u);
        q.setView( AppBeans.get(Metadata.class).getViewRepository().getView(Employee.class, "_local"));
        List list = q.getResultList();
        if (!list.isEmpty()) {
            phone = ((Employee) list.get(0)).getMobilePhone() == null ? "" : ((Employee) list.get(0)).getMobilePhone();
            if (phone.contains(",")) {
                String[] phones = phone.split(",");
                for (String p : phones) {
                    if (p.length() > 0) {
                        phone = p;
                        break;
                    }
                }
            }
            if (phone.startsWith("+7")) {
                phone = phone.substring(1, phone.length());
            } else if (phone.startsWith("8")) {
                phone = "7" + phone.substring(1, phone.length());
            }
        }
        q = em.createQuery("select e from df\$Employee e where e.user.id = :user").setParameter("user", currentUser);
        q.setView(AppBeans.get(Metadata.class).getViewRepository().getView(Employee.class, "_local"));
        list = q.getResultList();
        if (!list.isEmpty()) {
            Employee e = ((Employee) list.get(0));
            currentUserName.append(e.lastName ? e.lastName : "")
                    .append(e.firstName && e.firstName.length() > 0 ? e.firstName.substring(0, 1).toUpperCase() + "." : "")
                    .append(e.middleName && e.middleName.length() > 0 ? e.middleName.substring(0, 1).toUpperCase() + "." : "")
        }
        tx.commit();
    } catch (Exception e) {
        //empty
    } finally {
        tx.end();
    }
}

SmsSenderConfig ssc = AppBeans.get(Configuration.class).getConfig(SmsSenderConfig.class);
ThesisConfig tc = AppBeans.get(Configuration.class).getConfig(ThesisConfig.class);
String messagePattern = "%s(%s) %s %s";

if (currentUserName.length() == 0 && currentUser) {
    currentUserName.append(currentUser.lastName ? currentUser.lastName : "")
            .append(currentUser.firstName && currentUser.firstName.length() > 0 ? currentUser.firstName.substring(0, 1).toUpperCase() + "." : "")
            .append(currentUser.middleName && currentUser.middleName.length() > 0 ? currentUser.middleName.substring(0, 1).toUpperCase() + "." : "");
    if (currentUserName.length() == 0)
        currentUserName.append(currentUser.login);
}
String num = "";
String no = null;
if (!c.registered) {
    num = c.number;
} else {
    num = c.regNo
}
if (lang == 'ru') {
    no = "№";
} else {
    no = "#"
}
String message = (lang == 'ru') ? String.format(messagePattern, new StringBuilder(c instanceof Contract ? "Договор " : "Документ ").append(no).append(num).toString(),
        NotificationUtils.getLocState(c, lang), currentUserName.toString(), (c.comment ? ("'" + c.comment + "'") : "")) :
    String.format(messagePattern, new StringBuilder(c instanceof Contract ? "Contract " : "Document ").append(no).append(num).toString(),
            NotificationUtils.getLocState(c, lang), currentUserName.toString(), (c.comment ? ("'" + c.comment + "'") : ""))

if (tc.getPrintUrlInSms()) {
    message = NotificationUtils.makeLink(c, u) + " " + message
}

subject = phone;


int smsSize
if (lang == 'ru') {
    smsSize = ssc.getSmsMaxParts() == 1 ? 70 : ssc.getSmsMaxParts() * 67
} else {
    smsSize = ssc.getSmsMaxParts() == 1 ? 160 : ssc.getSmsMaxParts() * 153
}
int maxSmsSize = smsSize <= 255 ? smsSize : 255;

message = message.replaceAll("[ ]+", " ").trim()
if (message.length() <= maxSmsSize) {
    body = message
} else {
    body = message.substring(0, maxSmsSize - 3) + "..'"
}


String getInstanceName(Card card) {
    switch (card) {
        case Contract: return 'df$Contract'
        default: return 'df$SimpleDoc'
    }
}

