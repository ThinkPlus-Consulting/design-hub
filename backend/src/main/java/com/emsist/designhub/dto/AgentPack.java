package com.emsist.designhub.dto;
import com.emsist.designhub.domain.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AgentPack {
    private String packId;
    private int packVersion;
    private Instant generatedAt;
    private PackBaseline baseline;
    private UserStory story;
    private List<Screen> deliveredScreens;
    private List<ApiContract> deliveredApis;
    private List<DataEntity> deliveredEntities;
    private List<CodeAsset> codeTargets;
    private List<ApplicationComponent> components;
    private List<TestCase> testCases;
    private List<CodingConvention> conventions;
    private List<QualityConstraint> qualityConstraints;
    private PackCompleteness completeness;
}
