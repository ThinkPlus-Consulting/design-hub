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
public class Feature {

    @Id
    private String featureId;

    private String title;
    private String description;
    private String externalWorkflowState;
    private String externalOwner;
    private String targetIteration;
    private List<String> externalRefs;
    private Status status;

    @Relationship(type = "HAS_STORY", direction = Relationship.Direction.OUTGOING)
    private List<UserStory> stories;
}
