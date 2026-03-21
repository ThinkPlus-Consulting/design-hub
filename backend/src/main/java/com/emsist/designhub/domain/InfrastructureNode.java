package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfrastructureNode {

    @Id
    private String nodeId;

    private String name;
    private String nodeType;
    private String location;
    private Status status;
}
