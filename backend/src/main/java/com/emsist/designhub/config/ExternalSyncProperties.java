package com.emsist.designhub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "designhub.external-sync")
public class ExternalSyncProperties {

    private SchedulerProperties scheduler = new SchedulerProperties();
    private SourceProperties azureDevops = new SourceProperties();
    private SourceProperties jira = new SourceProperties();

    public SourceProperties sourceFor(String sourceSystem) {
        if (sourceSystem == null || sourceSystem.isBlank()) {
            throw new IllegalArgumentException("Source system is required");
        }

        return switch (sourceSystem.trim().toUpperCase(Locale.ROOT)) {
            case "AZURE_DEVOPS" -> azureDevops;
            case "JIRA" -> jira;
            default -> throw new IllegalArgumentException("Unsupported source system: " + sourceSystem);
        };
    }

    @Data
    public static class SourceProperties {
        private boolean enabled = true;
        private String baseUrl;
        private String organization;
        private String project;
        private String projectKey;
        private String token;
        private String pollPath;
        private Map<String, String> headers = new LinkedHashMap<>();
        private WebhookProperties webhook = new WebhookProperties();
        private PollingProperties polling = new PollingProperties();

        public boolean isTransportEnabled(String transportMode) {
            if (!enabled) {
                return false;
            }

            return switch (transportMode) {
                case "WEBHOOK" -> webhook.enabled;
                case "POLL" -> polling.enabled;
                default -> true;
            };
        }

        public boolean hasPollingEndpoint() {
            return baseUrl != null && !baseUrl.isBlank() && pollPath != null && !pollPath.isBlank();
        }
    }

    @Data
    public static class WebhookProperties {
        private boolean enabled = true;
        private String secret;
    }

    @Data
    public static class PollingProperties {
        private boolean enabled = true;
        private Duration interval = Duration.ofMinutes(15);
        private boolean dryRun = true;
        private String wiql;
        private String jql;
        private String updatedSince;
    }

    @Data
    public static class SchedulerProperties {
        private boolean enabled = true;
        private Duration fixedDelay = Duration.ofMinutes(1);
        private String requestedBy = "scheduler";
    }
}
