package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRule {
    @Id
    private String validationRuleId;
    private String fieldPath;
    private String validationType;
    private String expression;
    private String errorMessage;
    private Status status;
}
