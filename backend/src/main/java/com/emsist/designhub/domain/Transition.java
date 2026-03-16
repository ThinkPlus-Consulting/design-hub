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
public class Transition {

    @Id
    private String transitionId;

    private String name;
    private String description;
    private String transitionType;  // NAVIGATION, MODAL_OPEN, MODAL_CLOSE, TAB_SWITCH, REDIRECT
    private String guard;           // condition that must be true for the transition to fire
    private Status status;

    @Relationship(type = "FROM_SCREEN", direction = Relationship.Direction.OUTGOING)
    private Screen fromScreen;

    @Relationship(type = "TO_SCREEN", direction = Relationship.Direction.OUTGOING)
    private Screen toScreen;

    @Relationship(type = "CAUSED_BY_INTERACTION", direction = Relationship.Direction.OUTGOING)
    private Interaction causedByInteraction;
}
