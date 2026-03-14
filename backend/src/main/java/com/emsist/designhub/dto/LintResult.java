package com.emsist.designhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LintResult {
    private String file;
    private String artifactId;
    private String artifactType;
    private List<LintIssue> errors;
    private List<LintIssue> warnings;

    public boolean hasBlockingErrors() {
        return errors != null && !errors.isEmpty();
    }
}
