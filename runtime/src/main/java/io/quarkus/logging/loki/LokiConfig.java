package io.quarkus.logging.loki;

import java.util.Optional;
import java.util.logging.Level;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Configuration for Sentry logging.
 */
@ConfigRoot(phase = ConfigPhase.RUN_TIME, name = "log.loki")
public class LokiConfig {

    /**
     * Determine whether to enable the Loki logging extension.
     */
    @ConfigItem(name = ConfigItem.PARENT)
    boolean enable;

    /**
     * Loki host
     *
     * THe host your Loki server is on
     */
    @ConfigItem
    public Optional<String> host;

    /**
     * Loki port
     *
     * The port Loki is listening on. Usually port 3100
     */
    @ConfigItem
    public Optional<Integer> port;

    /**
     * App label
     *
     * If present, a label of app=\<appLabel> is supplied
     */
    @ConfigItem
    public Optional<String> appLabel;

    /**
     * The Loki log level.
     */
    @ConfigItem(defaultValue = "WARN")
    public Level level;

}
