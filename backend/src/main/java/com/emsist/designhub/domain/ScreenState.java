package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenState {

    @Id
    private String stateId;

    private String name;
    private String description;
    private String stateType;       // EMPTY, LOADING, ERROR, POPULATED, DISABLED
    private String entryCondition;
    private String exitCondition;
    private Status status;

    @Relationship(type = "BELONGS_TO_SCREEN", direction = Relationship.Direction.OUTGOING)
    private Screen belongsToScreen;
}
