package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    @Id
    private String personaId;     // Pattern: PER-{code}

    private String name;
    private String summary;
    private List<String> goals;
    private List<String> painPoints;
    private List<String> roleKeys;
    private Status status;
    private List<String> sourceRefs;
}
