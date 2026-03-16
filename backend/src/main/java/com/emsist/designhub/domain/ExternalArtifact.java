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
    private String url;
    private String syncStatus;
    private Instant lastSyncedAt;
    private Status status;

    @Relationship(type = "REPRESENTS_STORY", direction = Relationship.Direction.OUTGOING)
    private List<UserStory> representsStories;

    @Relationship(type = "REPRESENTS_BUG", direction = Relationship.Direction.OUTGOING)
    private List<Bug> representsBugs;
}
