package com.emsist.designhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class CodeAsset {
    @Id
    private String codeAssetId;

    private String filePath;
    private String assetType;
    private String language;
    private String fileFormat;
    private String layerType;
    private String packageName;
    private String className;
    private String description;
    private Status status;

    @Relationship(type = "ASSET_FOR_SCREEN", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"gaps", "contentElements", "transitionsTo", "storyRefs", "roleKeys", "personaIds"})
    private List<Screen> screensImplemented;

    @Relationship(type = "ASSET_FOR_API", direction = Relationship.Direction.OUTGOING)
    private List<ApiContract> apisImplemented;

    @Relationship(type = "ASSET_FOR_ENTITY", direction = Relationship.Direction.OUTGOING)
    private List<DataEntity> entitiesImplemented;

    @Relationship(type = "ASSET_FOR_RULE", direction = Relationship.Direction.OUTGOING)
    private List<Rule> rulesImplemented;

    @Relationship(type = "GOVERNED_BY_CONVENTION", direction = Relationship.Direction.OUTGOING)
    private List<CodingConvention> conventions;
}
