package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Event")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainEvent {

    @Id
    private String eventCode;

    private String displayName;
    private String payload;
}
