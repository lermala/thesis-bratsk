package com.company.bratsk;


import com.haulmont.thesis.testsupport.ThesisTestContainer;
import org.junit.jupiter.api.extension.ExtensionContext;

public class BratskTestContainer extends ThesisTestContainer {

    public BratskTestContainer() {
        super();
        // project and special properties for test environment.
        appPropertiesFiles.add("bratsk-app.properties");
        appPropertiesFiles.add("bratsk-test-app.properties");
        autoConfigureDataSource();
    }

    public static class Common extends BratskTestContainer {

        // A common singleton instance of the test container which is initialized once for all tests
        public static final BratskTestContainer.Common INSTANCE = new BratskTestContainer.Common();

        private static volatile boolean initialized;

        private Common() {}

        @Override
        public void beforeAll(ExtensionContext extensionContext) throws Exception {
            if (!initialized) {
                super.beforeAll(extensionContext);
                initialized = true;
            }
            setupContext();
        }

        @SuppressWarnings("RedundantThrows")
        @Override
        public void afterAll(ExtensionContext extensionContext) throws Exception {
            cleanupContext();
           // never stops - do not call super
        }
    }
}