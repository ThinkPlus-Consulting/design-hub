package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalArtifact {

    @Id
    private String externalId;

    private String system;
    private String externalType;
    private String key;
    private String title;
    private String projectScope;
    private String workflowState;
    private String priority;
    private String owner;
    private String reporter;
    private List<String> labels;
    private List<String> customFields;
    private String url;
    private String syncStatus;
    private Instant lastSyncedAt;
    private Status status;

    @Relationship(type = "REPRESENTS_STORY", direction = Relationship.Direction.OUTGOING)
    private List<UserStory> representsStories;

    @Relationship(type = "REPRESENTS_BUG", direction = Relationship.Direction.OUTGOING)
    private List<Bug> representsBugs;

    @Relationship(type = "REPRESENTS_FEATURE", direction = Relationship.Direction.OUTGOING)
    private List<Feature> representsFeatures;

    @Relationship(type = "REPRESENTS_TASK", direction = Relationship.Direction.OUTGOING)
    private List<Task> representsTasks;

    @Relationship(type = "PARENT_OF", direction = Relationship.Direction.OUTGOING)
    private List<ExternalArtifact> children;

    @Relationship(type = "CHILD_OF", direction = Relationship.Direction.OUTGOING)
    private List<ExternalArtifact> parents;

    @Relationship(type = "DEPENDS_ON", direction = Relationship.Direction.OUTGOING)
    private List<ExternalArtifact> dependencies;

    @Relationship(type = "RELATES_TO", direction = Relationship.Direction.OUTGOING)
    private List<ExternalArtifact> relatedArtifacts;

    @Relationship(type = "DUPLICATES", direction = Relationship.Direction.OUTGOING)
    private List<ExternalArtifact> duplicates;
}
