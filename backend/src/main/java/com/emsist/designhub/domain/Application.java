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
public class Application {
    @Id
    private String applicationId;
    private String name;
    private String description;
    private String repoPath;
    private String repoUrl;
    private String workspaceType;
    private String defaultBuildCommand;
    private String defaultTestCommand;
    private List<String> bootstrapSteps;
    private Status status;

    @Relationship(type = "HAS_COMPONENT", direction = Relationship.Direction.OUTGOING)
    private List<ApplicationComponent> components;

    @Relationship(type = "GOVERNED_BY_CONVENTION", direction = Relationship.Direction.OUTGOING)
    private List<CodingConvention> conventions;
}
