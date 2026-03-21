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
public class Finding {

    @Id
    private String findingId;

    private String summary;
    private String externalWorkflowState;
    private String externalPriority;
    private String externalOwner;
    private java.util.List<String> externalRefs;
    private Status status;
}
