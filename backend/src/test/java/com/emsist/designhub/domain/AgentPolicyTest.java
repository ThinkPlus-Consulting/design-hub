package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AgentPolicyTest {

    @Test
    void shouldBuildAgentPolicyWithAllAttributes() {
        var policy = AgentPolicy.builder()
                .policyId("POL-BACKEND-001")
                .name("Backend Service Agent Policy")
                .allowedRepos(List.of("design-hub"))
                .allowedCommands(List.of("mvn test", "mvn compile"))
                .forbiddenCommands(List.of("rm -rf", "docker push"))
                .allowedEnvironments(List.of("dev", "staging"))
                .secretScopes(List.of("NEO4J_URI"))
                .maxFilesTouched(20)
                .requiresHumanApproval(false)
                .approvalThreshold("MEDIUM")
                .build();

        assertEquals("POL-BACKEND-001", policy.getPolicyId());
        assertEquals(2, policy.getAllowedCommands().size());
        assertEquals(20, policy.getMaxFilesTouched());
    }

    @Test
    void shouldWireGovernedByPolicyOnApplication() {
        var policy = AgentPolicy.builder()
                .policyId("POL-DH-001")
                .name("Design Hub Policy")
                .build();
        var app = Application.builder()
                .applicationId("APP-DH")
                .policies(List.of(policy))
                .build();

        assertEquals(1, app.getPolicies().size());
    }
}
