package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JourneyStep {

    @Id
    @GeneratedValue
    private Long id;

    private String stepId;
    private String label;
    private String preCondition;
    private String postCondition;
    private String interactionRef;
    private int orderIndex;
}
