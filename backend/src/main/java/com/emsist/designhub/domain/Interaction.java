package com.emsist.designhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Interaction {

    @Id
    private String interactionId;

    private String surfaceId;
    private String element;
    private String trigger;
    private String permission;
    private String confirmationCode;

    private List<String> personaIds;
    private List<String> roleKeys;
    private List<String> apiCalls;

    @Relationship(type = "REQUIRES_PERMISSION", direction = Relationship.Direction.OUTGOING)
    private Permission requiresPermission;

    @Relationship(type = "ON_SCREEN", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private Screen onScreen;

    @Relationship(type = "HAS_EFFECT", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Effect> effects = new ArrayList<>();
}
