package io.quarkus.logging.loki;

import java.util.Optional;
import java.util.logging.Handler;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class LokiHandlerValueFactory {

    public RuntimeValue<Optional<Handler>> create(final LokiConfig config) {

        if (!config.enable) {
            System.err.println("--- Loki is not enabled ---");
            return new RuntimeValue<>(Optional.empty());
        }
        System.err.println("--- Loki is enabled ---");

        // Init Loki
        LokiHandler handler = new LokiHandler(config.host.orElse("localhost"), config.port.orElse(3100));
        handler.setLevel(config.level);
        handler.setAppLabel(config.appLabel.orElse(""));
        return new RuntimeValue<>(Optional.of(handler));
    }
}
