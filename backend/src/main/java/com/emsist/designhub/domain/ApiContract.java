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
public class ApiContract {
    @Id
    private String contractId;
    private String path;
    private String method;
    private String description;
    private String externalWorkflowState;
    private String externalOwner;
    private List<String> externalRefs;
    private Status status;

    @Relationship(type = "HAS_QUALITY_CONSTRAINT", direction = Relationship.Direction.OUTGOING)
    private List<QualityConstraint> qualityConstraints;

    @Relationship(type = "BASELINED_BY", direction = Relationship.Direction.OUTGOING)
    private List<EvidenceRecord> baselines;

    @Relationship(type = "HAS_REQUEST", direction = Relationship.Direction.OUTGOING)
    private List<RequestSchema> requestSchemas;

    @Relationship(type = "HAS_RESPONSE", direction = Relationship.Direction.OUTGOING)
    private List<ResponseSchema> responseSchemas;

    @Relationship(type = "HAS_ERROR", direction = Relationship.Direction.OUTGOING)
    private List<ErrorContract> errorContracts;
}
