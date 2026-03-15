package com.emsist.designhub.domain;

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
public class Journey {

    @Id
    private String journeyId;

    private String title;
    private String personaId;
    private String roleKey;
    private String goalStatement;

    private String designStatus;
    private String prototypeStatus;
    private String deliveryStatus;

    private Status status;

    @Relationship(type = "PERFORMED_BY_PERSONA", direction = Relationship.Direction.OUTGOING)
    private Persona performedByPersona;

    @Relationship(type = "HAS_STEP", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<JourneyStep> steps = new ArrayList<>();
}
