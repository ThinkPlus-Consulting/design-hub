package com.emsist.designhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Screen {

    @Id
    private String surfaceId;

    private String label;
    private String module;
    private String routePath;

    private String designStatus;      // COMPLETE, SPECIFIED, NOT_STARTED
    private String prototypeStatus;   // PROTOTYPED, NOT_STARTED
    private String deliveryStatus;    // INTEGRATED, TESTED, NOT_STARTED

    private Status status;

    private String wcag;
    private boolean responsive;
    private boolean roleAdaptive;
    private boolean deepLinkable;
    private boolean loadingStates;
    private int messageRegistryCount;

    private String notes;

    private List<String> storyRefs;
    private List<String> roleKeys;
    private List<String> personaIds;

    @Relationship(type = "USED_BY_PERSONA", direction = Relationship.Direction.OUTGOING)
    private List<Persona> usedByPersonas;

    @Relationship(type = "ACCESSIBLE_BY_ROLE", direction = Relationship.Direction.OUTGOING)
    private List<BusinessRole> accessibleByRoles;

    @Relationship(type = "HAS_GAP", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<Gap> gaps = new ArrayList<>();

    @Relationship(type = "HAS_CONTENT", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<ContentElement> contentElements = new ArrayList<>();

    @Relationship(type = "TRANSITIONS_TO", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"gaps", "contentElements", "transitionsTo"})
    @Builder.Default
    private List<Screen> transitionsTo = new ArrayList<>();

    @Relationship(type = "HAS_QUALITY_CONSTRAINT", direction = Relationship.Direction.OUTGOING)
    private List<QualityConstraint> qualityConstraints;

    @Relationship(type = "BASELINED_BY", direction = Relationship.Direction.OUTGOING)
    private List<EvidenceRecord> baselines;
}
