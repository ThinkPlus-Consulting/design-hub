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
public class ProcessGateway {
    @Id
    private String gatewayId;
    private String name;
    private String gatewayType;
    private String defaultFlowTarget;
    private Status status;

    @Relationship(type = "FLOWS_TO", direction = Relationship.Direction.OUTGOING)
    private List<ProcessActivity> flowsToActivities;
}
