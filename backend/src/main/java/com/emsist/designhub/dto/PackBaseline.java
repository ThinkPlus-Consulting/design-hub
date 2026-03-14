package com.emsist.designhub.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PackBaseline {
    private String repoCommit;
    private String graphSnapshotId;
    private String branch;
    private String dependencyManifest;
    private String requirementSnapshot;
}
