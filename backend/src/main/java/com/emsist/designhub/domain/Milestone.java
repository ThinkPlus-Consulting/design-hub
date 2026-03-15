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
public class Milestone {

    @Id
    private String milestoneId;   // Pattern: MS-{projectCode}-{seq}

    private String name;
    private String description;
    private MilestoneType milestoneType; // Frozen enum: SPRINT, PHASE, RELEASE_CUT, CHECKPOINT
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;

    @Relationship(type = "HAS_TASK", direction = Relationship.Direction.OUTGOING)
    private List<Task> tasks;
}
