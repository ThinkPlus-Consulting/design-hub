package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentElement {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String contentElementId;

    private String element;
    private String type;
    private String description;
    private int orderIndex;
}
