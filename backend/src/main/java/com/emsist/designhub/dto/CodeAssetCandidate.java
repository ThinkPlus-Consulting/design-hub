package com.emsist.designhub.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CodeAssetCandidate {
    private String filePath;
    private String assetType;
    private String language;
    private String componentId; // Owning ApplicationComponent
}
