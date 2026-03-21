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
public class BusinessObjective {

    @Id
    private String objectiveId;

    private String title;
    private Status status;
    private String externalWorkflowState;
    private String externalPriority;
    private String externalOwner;
    private List<String> externalRefs;
}
