package com.emsist.designhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Effect {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String effectId;

    private String type;           // navigate, redirect, filter, mutation, toast, etc.
    private String target;
    private String targetMode;
    private String resolutionRule;
    private String defaultTarget;

    @Relationship(type = "NAVIGATES_TO", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private Screen navigatesTo;
}
