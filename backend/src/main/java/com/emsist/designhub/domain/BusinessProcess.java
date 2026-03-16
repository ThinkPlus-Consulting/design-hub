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
public class BusinessProcess {
    @Id
    private String processId;
    private String name;
    private String description;
    private String diagramFormat;
    private String diagramPath;
    private String diagramVersion;
    private String diagramSource;
    private boolean isExecutableModel;
    private Status status;

    @Relationship(type = "HAS_FLOW_NODE", direction = Relationship.Direction.OUTGOING)
    private List<ProcessActivity> activities;

    @Relationship(type = "HAS_FLOW_NODE", direction = Relationship.Direction.OUTGOING)
    private List<ProcessGateway> gateways;

    @Relationship(type = "HAS_FLOW_NODE", direction = Relationship.Direction.OUTGOING)
    private List<ProcessEvent> events;
}
