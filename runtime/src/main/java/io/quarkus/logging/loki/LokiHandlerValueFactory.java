package io.quarkus.logging.loki;

import java.net.URI;
import java.util.Optional;
import java.util.logging.Handler;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import java.util.logging.Logger;

@Recorder
public class LokiHandlerValueFactory {

    Logger log = Logger.getLogger("LoggingLoki");

    public RuntimeValue<Optional<Handler>> create(final LokiConfig config) {

        if (!config.enable) {
            log.fine("--- Loki is not enabled ---");
            return new RuntimeValue<>(Optional.empty());
        }

        // Init Loki
        String lokiHost = config.host.orElse("localhost");
        Integer lokiPort = config.port.orElse(3100);
        String lokiUrl = "http://" + lokiHost + ":" + lokiPort + "/loki/api/v1/push";
        log.info("Sending to Loki at "  + lokiUrl);
        URI lokiUri = URI.create(lokiUrl);

        LokiHandler handler = new LokiHandler(lokiUri);
        handler.setLevel(config.level);
        handler.setAppLabel(config.appLabel.orElse(""));
        handler.setEnvironment(config.environment.orElse(""));
        return new RuntimeValue<>(Optional.of(handler));
    }
}
