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
public class Epic {

    @Id
    private String epicId;

    private String title;
    private String description;
    private Status status;

    @Relationship(type = "HAS_FEATURE", direction = Relationship.Direction.OUTGOING)
    private List<Feature> features;
}
