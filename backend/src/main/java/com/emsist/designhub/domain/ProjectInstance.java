package com.emsist.designhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInstance {

    @Id
    private String projectId;     // Pattern: PROJ-{code}-{seq}

    private String name;
    private String description;
    private String projectType;   // GREENFIELD, ENHANCEMENT, MIGRATION, INTEGRATION
    private LocalDate startDate;
    private LocalDate targetDate;
    private Status status;

    @Relationship(type = "TARGETS_CAPABILITY", direction = Relationship.Direction.OUTGOING)
    private List<BusinessCapability> targetCapabilities;

    @Relationship(type = "ADDRESSES_GAP", direction = Relationship.Direction.OUTGOING)
    private List<Gap> addressedGaps;

    @Relationship(type = "HAS_PORTFOLIO", direction = Relationship.Direction.OUTGOING)
    private RequirementPortfolio portfolio;

    @Relationship(type = "HAS_TASK", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"implementsAssets"})
    private List<Task> tasks;

    @Relationship(type = "HAS_MILESTONE", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"tasks"})
    private List<Milestone> milestones;

    @Relationship(type = "CREATES_APPLICATION", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"components", "conventions", "policies"})
    private List<Application> createdApplications;

    @Relationship(type = "ENHANCES_APPLICATION", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"components", "conventions", "policies"})
    private List<Application> enhancedApplications;

    @Relationship(type = "INTEGRATES_WITH", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"components", "conventions", "policies"})
    private List<Application> integratedApplications;

    @Relationship(type = "CREATES_COMPONENT", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"codeAssets", "conventions", "qualityConstraints", "policies"})
    private List<ApplicationComponent> createdComponents;

    @Relationship(type = "ENHANCES_COMPONENT", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"codeAssets", "conventions", "qualityConstraints", "policies"})
    private List<ApplicationComponent> enhancedComponents;
}
