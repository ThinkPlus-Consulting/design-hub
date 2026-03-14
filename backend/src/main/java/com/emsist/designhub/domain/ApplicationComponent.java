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
public class ApplicationComponent {
    @Id
    private String componentId;
    private String name;
    private String description;
    private String componentType;
    private String frameworkFamily;
    private String frameworkName;
    private String frameworkVersion;
    private String runtime;
    private String language;
    private String languageVersion;
    private String modulePath;
    private String manifestPath;
    private String buildCommand;
    private String testCommand;
    private String entrypointPath;
    private Status status;

    @Relationship(type = "HAS_CODE_ASSET", direction = Relationship.Direction.OUTGOING)
    private List<CodeAsset> codeAssets;

    @Relationship(type = "GOVERNED_BY_CONVENTION", direction = Relationship.Direction.OUTGOING)
    private List<CodingConvention> conventions;

    @Relationship(type = "HAS_QUALITY_CONSTRAINT", direction = Relationship.Direction.OUTGOING)
    private List<QualityConstraint> qualityConstraints;
}
