package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessActivity {
    @Id
    private String activityId;
    private String name;
    private String description;
    private String activityType;
    private String actionType;
    private String taskNature;
    private int orderIndex;
    private String trigger;
    private String preCondition;
    private String postCondition;
    private Status status;

    @Relationship(type = "FLOWS_TO", direction = Relationship.Direction.OUTGOING)
    private List<ProcessActivity> flowsToActivities;

    @Relationship(type = "EXPANDS_TO", direction = Relationship.Direction.OUTGOING)
    private List<BusinessProcess> expandsTo;

    @Relationship(type = "CALLS_PROCESS", direction = Relationship.Direction.OUTGOING)
    private List<BusinessProcess> callsProcess;
}
