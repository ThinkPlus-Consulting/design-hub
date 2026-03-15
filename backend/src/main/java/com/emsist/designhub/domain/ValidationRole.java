package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRole {

    @Id
    private String validationRoleKey;   // HITL_REVIEWER, AUDITOR

    private String displayName;
    private String scope;
    private Status status;
    private List<String> sourceRefs;
}
