package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    @Id
    private String assessmentId;

    private String name;
    private AssessmentType assessmentType;
    private TargetKind targetKind;
    private LocalDate assessmentDate;
    private String assessor;
    private MaturityLevel maturityLevel;
    private String currentStateDescription;
    private String targetStateDescription;
    private Integer score;
    private Status status;

    @Relationship(type = "IDENTIFIES_GAP", direction = Relationship.Direction.OUTGOING)
    private List<Gap> identifiedGaps;
}
