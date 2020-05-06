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

import io.vertx.core.json.Json;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.jboss.logmanager.ExtLogRecord;


/**
 * @author hrupp
 */
public class LokiHandler extends Handler {

    HttpClient client;
    private String appLabel;
    private URI lokiUri;
    private String environment;


    public LokiHandler(String lokiHost, Integer lokiPort) {

        String lokiUrl = "http://" + lokiHost + ":" + lokiPort + "/loki/api/v1/push";
        System.out.println("Sending to loki at "  + lokiUrl);
        lokiUri = URI.create(lokiUrl);

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                //          .proxy(ProxySelector.of(new InetSocketAddress(lokiHost, lokiPort)))
                //          .authenticator(Authenticator.getDefault())
                .build();
    }

    @Override
    public void publish(LogRecord record) {

        // Skip messages that are below the configured threshold
        if (record.getLevel().intValue() < getLevel().intValue()) {
            return;
        }

        Map<String, String> tags = new HashMap<>();

        String host = record instanceof ExtLogRecord ? ((ExtLogRecord) record).getHostName() : null;
        if (record.getLoggerName().equals("__AccessLog")) {
            tags.put("type", "access");
        }
        if (host != null && !host.isEmpty()) {
            tags.put("host", host);
        }
        if (appLabel != null && !appLabel.isEmpty()) {
            tags.put("app", appLabel);
        }

        if (environment != null && !environment.isEmpty()) {
            tags.put("env",environment);
        }

        if (record instanceof LokiLogRecord) {
            tags.putAll(((LokiLogRecord) record).getTags());
        }

        tags.put("level", record.getLevel().getName());

        String msg;
        if (record.getParameters() != null && record.getParameters().length > 0) {
            switch (((ExtLogRecord) record).getFormatStyle()) {
                case PRINTF:
                    msg = String.format(record.getMessage(), record.getParameters());
                    break;
                case MESSAGE_FORMAT:
                    msg = MessageFormat.format(record.getMessage(), record.getParameters());
                    break;
                default: // == NO_FORMAT
                    msg = record.getMessage();
            }
        } else {
            msg = record.getMessage();
        }

        String body = assemblePayload(msg, tags);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(lokiUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 204) {
                System.out.println(response.statusCode());
                System.out.println(response.body());
            }
        } catch (Exception ioe) {
            System.err.println("Sending to loki failed: " + ioe.getMessage());
            System.out.println(body);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    private String assemblePayload(String message, Map<String, String> tags) {

        List<Object> values = new ArrayList<>(2);
        values.add(System.currentTimeMillis() + "000000"); // Golang Nanos
        values.add(message);

        List<Object> valuesList = new ArrayList<>();
        valuesList.add(values);

        Map<String, Object> stream2Object = new HashMap<>();
        stream2Object.put("stream", tags);
        stream2Object.put("values", valuesList);

        List<Object> streamsArray = new ArrayList<>();
        streamsArray.add(stream2Object);

        Map<String, Object> job = new HashMap<>();
        job.put("streams", streamsArray);

        String body = Json.encodePrettily(job);
        return body;
    }

    public void setAppLabel(String label) {
        if (label != null) {
            this.appLabel = label;
        }
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
