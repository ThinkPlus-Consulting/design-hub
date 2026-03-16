package com.emsist.designhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

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

    @Relationship(type = "EXECUTES_INTERACTION", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"usedByPersonas", "accessibleByRoles", "requiresPermission", "callsApi",
            "triggersConfirmation", "onErrorShows", "onScreen", "effects"})
    private Interaction executesInteraction;

    @Relationship(type = "USES_SCREEN", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"gaps", "contentElements", "transitionsTo", "storyRefs", "roleKeys", "personaIds",
            "usedByPersonas", "accessibleByRoles", "messages", "qualityConstraints", "baselines",
            "validationRules", "canProduceErrors", "sourceReferences", "interactions", "deliveredByStories"})
    private Screen usesScreen;

    @Relationship(type = "STARTS_AT_TOUCHPOINT", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"usedByPersonas", "accessibleByRoles", "targetScreen", "entryModes"})
    private Touchpoint startsAtTouchpoint;
}
