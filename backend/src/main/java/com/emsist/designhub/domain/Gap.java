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
public class Gap {

    @Id
    private String gapId;       // Pattern: GAP-{parent}-{seq}

    private String gapType;     // MISSING_ARTIFACT, MISSING_RELATIONSHIP, MISSING_ATTRIBUTE, MISSING_RULE, CAPABILITY_GAP, PROCESS_GAP
    private String severity;    // CRITICAL, HIGH, MEDIUM, LOW
    private String description;
    private Status status;
}
