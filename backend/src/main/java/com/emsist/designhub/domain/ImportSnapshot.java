package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import java.time.Instant;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportSnapshot {
    @Id
    private String snapshotId;          // Pattern: IMP-{YYYYMMDD}-{seq}

    private String sourceType;          // GIT_DOC, JIRA_SYNC, MANUAL_ENTRY
    private String sourcePath;          // Optional — relative to repo root for GIT_DOC
    private Instant importedAt;         // ISO 8601
    private String importedBy;          // Agent or user who triggered import
    private String result;              // SUCCESS, PARTIAL, FAILED, CONFLICTED
    private Integer itemCount;          // Optional — number of items processed
    private String errorSummary;        // Optional — error details for PARTIAL/FAILED/CONFLICTED
    private String contentHash;         // Optional — hash of doc-authored fields for drift detection

    // IMPORTED_BY edge is modeled on the source side (importable T1 nodes point here)
    // No outbound @Relationship needed on ImportSnapshot itself
}
