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
    private String outcomeSuccess;
    private String outcomeError;
    private String outcomeLoading;
    private String errorCodeRef;

    private List<String> personaIds;
    private List<String> roleKeys;
    private List<String> apiCalls;

    @Relationship(type = "USED_BY_PERSONA", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Persona> usedByPersonas = new ArrayList<>();

    @Relationship(type = "ACCESSIBLE_BY_ROLE", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<BusinessRole> accessibleByRoles = new ArrayList<>();

    @Relationship(type = "REQUIRES_PERMISSION", direction = Relationship.Direction.OUTGOING)
    private Permission requiresPermission;

    @Relationship(type = "CALLS_API", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<ApiContract> callsApi = new ArrayList<>();

    @Relationship(type = "TRIGGERS_CONFIRMATION", direction = Relationship.Direction.OUTGOING)
    private ConfirmationDialog triggersConfirmation;

    @Relationship(type = "ON_ERROR_SHOWS", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<ErrorCode> onErrorShows = new ArrayList<>();

    @Relationship(type = "ON_SCREEN", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private Screen onScreen;

    @Relationship(type = "HAS_EFFECT", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Effect> effects = new ArrayList<>();
}
