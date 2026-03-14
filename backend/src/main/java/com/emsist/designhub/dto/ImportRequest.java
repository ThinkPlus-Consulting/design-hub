package com.emsist.designhub.dto;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ImportRequest {
    private List<String> sources;
    private ImportMode mode;
    private ConflictStrategy conflictStrategy;
    public enum ImportMode { FULL, INCREMENTAL, DRY_RUN }
    public enum ConflictStrategy { QUEUE, OVERWRITE, SKIP }
}
