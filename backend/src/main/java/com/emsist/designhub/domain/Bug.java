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
public class Bug {

    @Id
    private String bugId;

    private String externalKey;
    private String summary;
    private String severity;
    private Status status;

    @Relationship(type = "AFFECTS_SCREEN", direction = Relationship.Direction.OUTGOING)
    private List<Screen> affectsScreens;

    @Relationship(type = "HAS_SOURCE", direction = Relationship.Direction.OUTGOING)
    private List<SourceReference> sourceReferences;
}
