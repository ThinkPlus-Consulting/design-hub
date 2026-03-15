package com.emsist.designhub.domain;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import java.time.Instant;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceRecord {
    @Id
    private String evidenceId;

    private String evidenceType;     // TEST_RESULT, SCREENSHOT, CONTRACT_SNAPSHOT, VISUAL_REGRESSION
    private String artifactId;       // The requirement/test this proves
    private Instant producedAt;
    private String producedBy;       // Agent or user
    private String repoCommit;       // Git SHA when proof was produced
    private String result;           // PASS, FAIL, PARTIAL
    private String artifactPath;     // Path to proof artifact
}
