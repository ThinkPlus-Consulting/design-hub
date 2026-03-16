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
public class ProcessEvent {
    @Id
    private String eventId;
    private String name;
    private String eventPosition;
    private String eventTrigger;
    private boolean isInterrupting;
    private String attachedToRef;
    private Status status;

    @Relationship(type = "ATTACHED_TO", direction = Relationship.Direction.OUTGOING)
    private List<ProcessActivity> attachedTo;

    @Relationship(type = "FLOWS_TO", direction = Relationship.Direction.OUTGOING)
    private List<ProcessActivity> flowsToActivities;
}
