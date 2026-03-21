package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Node("Enum")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumDefinition {

    @Id
    private String enumId;

    private String name;
    private List<String> values;
}
