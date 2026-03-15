package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequirementPortfolio {

    @Id
    private String portfolioId;

    private String name;
    private String description;
    private Status status;

    @Relationship(type = "HAS_EPIC", direction = Relationship.Direction.OUTGOING)
    private List<Epic> epics;
}
