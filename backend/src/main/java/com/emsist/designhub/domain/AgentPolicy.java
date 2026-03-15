package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPolicy {
    @Id
    private String policyId;

    private String name;
    private List<String> allowedRepos;
    private List<String> allowedCommands;
    private List<String> forbiddenCommands;
    private List<String> allowedEnvironments;
    private List<String> secretScopes;
    private Integer maxFilesTouched;
    private Boolean requiresHumanApproval;
    private String approvalThreshold;
}
