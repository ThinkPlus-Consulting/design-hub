package com.emsist.designhub.service;

import com.emsist.designhub.config.LintRuleConfig;
import com.emsist.designhub.dto.LintIssue;
import com.emsist.designhub.dto.LintResult;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class RequirementLinterService {

    private final FrontmatterParser parser;
    private final LintRuleConfig config;

    public RequirementLinterService(FrontmatterParser parser, LintRuleConfig config) {
        this.parser = parser;
        this.config = config;
    }

    public LintResult lint(String markdownContent, String filePath) {
        List<LintIssue> errors = new ArrayList<>();
        List<LintIssue> warnings = new ArrayList<>();

        var fmOpt = parser.parse(markdownContent);
        if (fmOpt.isEmpty()) {
            errors.add(LintIssue.builder()
                    .rule("frontmatter-required").line(1)
                    .message("Document must have YAML frontmatter with id, type, status, version")
                    .severity(LintIssue.Severity.ERROR).autoFixable(false).build());
            return LintResult.builder().file(filePath)
                    .artifactId("UNKNOWN").artifactType("UNKNOWN")
                    .errors(errors).warnings(warnings).build();
        }

        var fm = fmOpt.get();

        // Validate ID pattern and required sections for known artifact types
        if (config.getArtifactTypes() != null && config.getArtifactTypes().containsKey(fm.getType())) {
            var rules = config.getArtifactTypes().get(fm.getType());

            if (rules.getIdPattern() != null && !Pattern.matches(rules.getIdPattern(), fm.getId())) {
                errors.add(LintIssue.builder()
                        .rule("stable-id-format").line(2)
                        .message("ID '" + fm.getId() + "' does not match pattern: " + rules.getIdPattern())
                        .severity(LintIssue.Severity.ERROR).autoFixable(false).build());
            }

            // Check required sections
            String body = parser.extractBody(markdownContent);
            if (rules.getRequiredSections() != null) {
                for (String section : rules.getRequiredSections()) {
                    if (!body.contains("## " + section)) {
                        warnings.add(LintIssue.builder()
                                .rule("required-section-missing").line(0)
                                .message("Missing required section: " + section)
                                .severity(LintIssue.Severity.WARNING).autoFixable(false).build());
                    }
                }
            }
        }

        return LintResult.builder().file(filePath)
                .artifactId(fm.getId()).artifactType(fm.getType())
                .errors(errors).warnings(warnings).build();
    }
}
