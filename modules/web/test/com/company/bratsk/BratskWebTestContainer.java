package com.company.bratsk;

import com.google.common.collect.Lists;
import com.haulmont.cuba.web.testsupport.TestContainer;

public class BratskWebTestContainer extends TestContainer {

    public BratskWebTestContainer() {
        appComponents = Lists.newArrayList("com.haulmont.thesis.app");
        appPropertiesFiles = Lists.newArrayList(
            // List the files defined in your web.xml
            // in appPropertiesConfig context parameter of the web module
            // Add properties file used in test environment in the end of the list
            "com/haulmont/cuba/web-app.properties",
            "thesis-web-app.properties",
            "bratsk-web-app.properties",
            "bratsk-test-web-app.properties"
        );
        springConfig="";
    }

    public static class Common extends BratskWebTestContainer {

        // A common singleton instance of the test container which is initialized once for all tests
        public static final BratskWebTestContainer.Common INSTANCE = new BratskWebTestContainer.Common();

        private static volatile boolean initialized;

        private Common() {}

        @Override
        public void before() throws Throwable {
            if (!initialized) {
                super.before();
                initialized = true;
            }
            setupContext();
        }

        @Override
        public void after() {
            cleanupContext();
            // never stops - do not call super
        }
    }
}