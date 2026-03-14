package com.emsist.designhub.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LintResultTest {

    @Test
    void shouldCreateLintIssueWithAllFields() {
        var issue = LintIssue.builder()
                .rule("stable-id-format")
                .line(3)
                .message("ID must match pattern US-{module}-{seq}")
                .severity(LintIssue.Severity.ERROR)
                .autoFixable(false)
                .build();

        assertEquals("stable-id-format", issue.getRule());
        assertEquals(3, issue.getLine());
        assertEquals(LintIssue.Severity.ERROR, issue.getSeverity());
        assertFalse(issue.isAutoFixable());
    }

    @Test
    void shouldCreateLintResultWithErrorsAndWarnings() {
        var error = LintIssue.builder()
                .rule("stable-id-format").line(1)
                .message("Missing id").severity(LintIssue.Severity.ERROR)
                .autoFixable(false).build();
        var warning = LintIssue.builder()
                .rule("missing-execution-mode").line(5)
                .message("No executionMode").severity(LintIssue.Severity.WARNING)
                .autoFixable(false).build();

        var result = LintResult.builder()
                .file("docs/stories/US-SCR-042.md")
                .artifactId("US-SCR-042")
                .artifactType("UserStory")
                .errors(List.of(error))
                .warnings(List.of(warning))
                .build();

        assertEquals("US-SCR-042", result.getArtifactId());
        assertEquals(1, result.getErrors().size());
        assertEquals(1, result.getWarnings().size());
        assertTrue(result.hasBlockingErrors());
    }

    @Test
    void shouldReportNoBlockingErrorsWhenClean() {
        var result = LintResult.builder()
                .file("docs/stories/US-SCR-001.md")
                .artifactId("US-SCR-001")
                .artifactType("UserStory")
                .errors(List.of())
                .warnings(List.of())
                .build();

        assertFalse(result.hasBlockingErrors());
    }
}
