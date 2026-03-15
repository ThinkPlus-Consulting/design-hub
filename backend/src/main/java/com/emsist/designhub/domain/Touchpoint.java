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
public class Touchpoint {

    @Id
    private String touchpointId;

    private String label;
    private String surfaceId;

    private List<String> personaIds;
    private List<String> roleKeys;

    @Relationship(type = "USED_BY_PERSONA", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Persona> usedByPersonas = new ArrayList<>();

    @Relationship(type = "ACCESSIBLE_BY_ROLE", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<BusinessRole> accessibleByRoles = new ArrayList<>();

    @Relationship(type = "DELIVERED_VIA_CHANNEL", direction = Relationship.Direction.OUTGOING)
    private Channel deliveredViaChannel;

    @Relationship(type = "TARGETS", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private Screen targetScreen;

    @Relationship(type = "HAS_ENTRY_MODE", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<EntryMode> entryModes = new ArrayList<>();
}
