package com.emsist.designhub.dto;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TestDiscoveryResult {
    private String testFilePath;
    private String testClassName;
    private List<String> testMethodNames;
    private String testFramework;
    private String componentId;
}
