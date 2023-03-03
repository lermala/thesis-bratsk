/*
 * Copyright (c) 2023 LTD Haulmont Samara. All Rights Reserved.
 * Haulmont Samara proprietary and confidential.
 * Use is subject to license terms.
 */

package process.mpaEndorsement

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.thesis.core.notification.NotificationParamBuilderAPI
import com.haulmont.thesis.core.notification.NotificationScriptType
import com.haulmont.thesis.core.notification.NotificationTemplateAPI
import com.haulmont.thesis.core.notification.TemplateBuilder

def template = TemplateBuilder.create()
        .add(TemplateBuilder.USER_NAME)
        .add(TemplateBuilder.MAIN_MSG)
        .add(TemplateBuilder.FINISH_BEFORE_DATE)
        .add(TemplateBuilder.CONTRACTOR)
        .add(TemplateBuilder.THEME)
        .add(TemplateBuilder.CARD_CONTENT)
        .add(TemplateBuilder.SENDER_COMMENT)
        .add(TemplateBuilder.PARTICIPANTS)
        .add(TemplateBuilder.FOOTER)
        .build();

binding.setVariable("notificationType", NotificationScriptType.ASSIGNMENT)
binding.setVariable("body", AppBeans.get(NotificationTemplateAPI.class).processTemplate(template, binding.getVariables()))
binding.setVariable("subject", AppBeans.get(NotificationParamBuilderAPI.class).getSubject(binding.getVariables()));
