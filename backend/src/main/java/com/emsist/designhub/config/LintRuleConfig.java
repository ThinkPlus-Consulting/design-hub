package com.emsist.designhub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "designhub.lint")
public class LintRuleConfig {
    private Map<String, ArtifactRules> artifactTypes;

    @Data
    public static class ArtifactRules {
        private String idPattern;
        private List<String> requiredSections;
        private boolean requireExecutionMode;
    }
}
