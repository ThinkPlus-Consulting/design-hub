package com.emsist.designhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LintIssue {
    private String rule;
    private int line;
    private String message;
    private Severity severity;
    private boolean autoFixable;

    public enum Severity { ERROR, WARNING, INFO }
}
