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
public class UserStory {

    @Id
    private String storyId;

    private String label;
    private String module;
    private String domain;
    private String storyNumber;

    @Relationship(type = "HAS_CRITERION", direction = Relationship.Direction.OUTGOING)
    private List<AcceptanceCriterion> criteria;
}
