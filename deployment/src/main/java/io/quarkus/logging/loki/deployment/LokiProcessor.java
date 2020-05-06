package io.quarkus.logging.loki.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LogHandlerBuildItem;
import io.quarkus.logging.loki.LokiConfig;
import io.quarkus.logging.loki.LokiHandlerValueFactory;

class LokiProcessor {

    private static final String FEATURE = "loki";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    LogHandlerBuildItem addLokiLogHandler(final LokiConfig lokiConfig,
            final LokiHandlerValueFactory lokiHandlerValueFactory) {
        System.err.println("--- LokiProcessor ---");
        return new LogHandlerBuildItem(lokiHandlerValueFactory.create(lokiConfig));
    }

    //    @BuildStep
    //    ExtensionSslNativeSupportBuildItem activateSslNativeSupport() {
    //        return new ExtensionSslNativeSupportBuildItem(FEATURE);
    //    }

}
