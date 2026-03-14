package com.emsist.designhub.service;

import com.emsist.designhub.config.LintRuleConfig;
import com.emsist.designhub.dto.LintIssue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class RequirementLinterServiceTest {

    private RequirementLinterService linter;

    @BeforeEach
    void setUp() {
        var config = new LintRuleConfig();
        var storyRules = new LintRuleConfig.ArtifactRules();
        storyRules.setIdPattern("US-[A-Z]+-\\d+");
        storyRules.setRequiredSections(List.of("Description", "Acceptance Criteria", "Deliverables", "Verification"));
        storyRules.setRequireExecutionMode(false);
        config.setArtifactTypes(Map.of("UserStory", storyRules));

        linter = new RequirementLinterService(new FrontmatterParser(), config);
    }

    @Test
    void shouldPassValidUserStory() {
        String doc = """
                ---
                id: US-SCR-042
                type: UserStory
                status: DEFINED
                version: 1
                ---
                ## Description
                As a user...
                ## Acceptance Criteria
                - Given...
                ## Deliverables
                - SCR-SETTINGS-01
                ## Verification
                - TC-SCR-042-01
                """;

        var result = linter.lint(doc, "docs/stories/US-SCR-042.md");
        assertFalse(result.hasBlockingErrors());
        assertEquals("US-SCR-042", result.getArtifactId());
    }

    @Test
    void shouldRejectMissingFrontmatter() {
        String doc = "# No frontmatter\nContent only.";
        var result = linter.lint(doc, "docs/stories/bad.md");
        assertTrue(result.hasBlockingErrors());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getRule().equals("frontmatter-required")));
    }

    @Test
    void shouldRejectInvalidIdPattern() {
        String doc = """
                ---
                id: bad-id-format
                type: UserStory
                status: DEFINED
                version: 1
                ---
                ## Description
                Content.
                ## Acceptance Criteria
                - Given...
                ## Deliverables
                - SCR-001
                ## Verification
                - TC-001
                """;

        var result = linter.lint(doc, "docs/stories/bad.md");
        assertTrue(result.hasBlockingErrors());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getRule().equals("stable-id-format")));
    }

    @Test
    void shouldWarnOnMissingSections() {
        String doc = """
                ---
                id: US-SCR-043
                type: UserStory
                status: DEFINED
                version: 1
                ---
                ## Description
                As a user...
                """;

        var result = linter.lint(doc, "docs/stories/US-SCR-043.md");
        // Missing sections are warnings, not errors
        assertFalse(result.hasBlockingErrors());
        assertTrue(result.getWarnings().size() >= 3); // Missing AC, Deliverables, Verification
    }
}
