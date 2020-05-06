/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkus.logging.loki;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jboss.logmanager.ExtLogRecord;

/**
 * @author hrupp
 */
public class LokiLogRecord extends ExtLogRecord {

    private Map<String, String> tags;

    public LokiLogRecord(Level level, String msg) {
        super(level, msg, "Bla?"); // TODO what does this loggerClassName provide?
    }

    public LokiLogRecord(Level level, String msg, String loggerClassName) {
        super(level, msg, loggerClassName);
    }

    public LokiLogRecord(Level level, String msg, FormatStyle formatStyle, String loggerClassName) {
        super(level, msg, formatStyle, loggerClassName);
    }

    public LokiLogRecord(ExtLogRecord original) {
        super(original);
    }

    public void addTag(String key, String value) {
        if (tags == null) {
            tags = new HashMap<>();
        }
        tags.put(key, value);
    }

    public Map<String, String> getTags() {
        if (tags == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(tags);
        }
    }
}
