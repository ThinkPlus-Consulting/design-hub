package com.emsist.designhub.service;

import com.emsist.designhub.dto.GraphObjectSummaryResponse;
import com.emsist.designhub.dto.GraphRelationExpansionResponse;
import com.emsist.designhub.dto.JourneyTraversalResponse;
import com.emsist.designhub.dto.PersonaSummaryResponse;
import com.emsist.designhub.dto.PersonaTraversalResponse;
import com.emsist.designhub.dto.ApplicationArchitectureResponse;
import com.emsist.designhub.dto.ApplicationSummaryResponse;
import com.emsist.designhub.dto.BusinessArchitectureResponse;
import com.emsist.designhub.dto.BusinessCapabilitySummaryResponse;
import com.emsist.designhub.dto.ChannelSummaryResponse;
import com.emsist.designhub.dto.ChannelTraversalResponse;
import com.emsist.designhub.dto.DataArchitectureObjectSummaryResponse;
import com.emsist.designhub.dto.DataArchitectureResponse;
import com.emsist.designhub.dto.ExternalArtifactSummaryResponse;
import com.emsist.designhub.dto.ExternalArtifactTraversalResponse;
import com.emsist.designhub.dto.InfrastructureDeploymentSummaryResponse;
import com.emsist.designhub.dto.InfrastructureArchitectureResponse;
import com.emsist.designhub.dto.TraceabilityStoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphQueryServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @InjectMocks
    private GraphQueryService service;

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForRequestedType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(Map.of(
                "id", "SCR-AUTH",
                "nodeType", "Screen",
                "displayName", "Login / Sign In",
                "status", "APPROVED",
                "module", "core",
                "domain", "auth",
                "routePath", "/auth/login",
                "relationCount", 6L
        )));

        List<GraphObjectSummaryResponse> responses = service.getObjects("screen", "APPROVED", "core", "login", 25);

        assertEquals(1, responses.size());
        assertEquals("SCR-AUTH", responses.get(0).id());
        assertEquals("Screen", responses.get(0).nodeType());
        assertEquals(6L, responses.get(0).relationCount());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:Screen)")
                        && cypher.contains("COUNT { (n)--() } AS relationCount")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForEpicType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "EPIC-AI-001");
        row.put("nodeType", "Epic");
        row.put("displayName", "Agent builder and orchestration");
        row.put("status", "IN_IMPLEMENTATION");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 2L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("epic", null, null, "builder", 10);

        assertEquals(1, responses.size());
        assertEquals("EPIC-AI-001", responses.get(0).id());
        assertEquals("Epic", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:Epic)")
                        && cypher.contains("n.epicId")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForDecisionType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "DEC-001");
        row.put("nodeType", "Decision");
        row.put("displayName", "Use agent pack export as the canonical automation contract");
        row.put("status", "APPROVED");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 5L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("decision", null, null, "canonical", 10);

        assertEquals(1, responses.size());
        assertEquals("DEC-001", responses.get(0).id());
        assertEquals("Decision", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:Decision)")
                        && cypher.contains("n.decisionId")
                        && cypher.contains("n.title")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForBusinessDomainType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "DOM-DESIGN");
        row.put("nodeType", "BusinessDomain");
        row.put("displayName", "Design Management");
        row.put("status", "");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 1L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("business-domain", null, null, "design", 10);

        assertEquals(1, responses.size());
        assertEquals("DOM-DESIGN", responses.get(0).id());
        assertEquals("BusinessDomain", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:BusinessDomain)")
                        && cypher.contains("n.domainCode")
                        && cypher.contains("n.name")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForScreenStateType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "STATE-SCR-AUTH-LOADING");
        row.put("nodeType", "ScreenState");
        row.put("displayName", "Authenticating");
        row.put("status", "DEFINED");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 1L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("screen-state", null, null, "auth", 10);

        assertEquals(1, responses.size());
        assertEquals("STATE-SCR-AUTH-LOADING", responses.get(0).id());
        assertEquals("ScreenState", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:ScreenState)")
                        && cypher.contains("n.stateId")
                        && cypher.contains("n.name")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForProjectType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "PROJ-DH-AI-001");
        row.put("nodeType", "ProjectInstance");
        row.put("displayName", "Design Hub AI builder delivery wave");
        row.put("status", "IN_IMPLEMENTATION");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 7L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("project", null, null, "builder", 10);

        assertEquals(1, responses.size());
        assertEquals("PROJ-DH-AI-001", responses.get(0).id());
        assertEquals("ProjectInstance", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:ProjectInstance)")
                        && cypher.contains("n.projectId")
                        && cypher.contains("n.name")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForTopicType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "TOP-001");
        row.put("nodeType", "Topic");
        row.put("displayName", "AI agent composition");
        row.put("status", "APPROVED");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 3L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("topic", null, null, "agent", 10);

        assertEquals(1, responses.size());
        assertEquals("TOP-001", responses.get(0).id());
        assertEquals("Topic", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:Topic)")
                        && cypher.contains("n.topicId")
                        && cypher.contains("n.name")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForJourneyStepType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "JRN-R05-001.06");
        row.put("nodeType", "JourneyStep");
        row.put("displayName", "Save draft");
        row.put("status", "DEFINED");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 4L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("journey-step", null, null, "draft", 10);

        assertEquals(1, responses.size());
        assertEquals("JRN-R05-001.06", responses.get(0).id());
        assertEquals("JourneyStep", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:JourneyStep)")
                        && cypher.contains("n.stepId")
                        && cypher.contains("n.label")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForLocaleType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "ar");
        row.put("nodeType", "Locale");
        row.put("displayName", "Arabic");
        row.put("status", "");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 1L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("locale", null, null, "arabic", 10);

        assertEquals(1, responses.size());
        assertEquals("ar", responses.get(0).id());
        assertEquals("Locale", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:Locale)")
                        && cypher.contains("n.localeCode")
                        && cypher.contains("n.displayName")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForIntegrationType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "INTG-001");
        row.put("nodeType", "Integration");
        row.put("displayName", "Design Hub to Identity Platform sign-in integration");
        row.put("status", "IMPLEMENTED");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 2L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("integration", null, null, "identity", 10);

        assertEquals(1, responses.size());
        assertEquals("INTG-001", responses.get(0).id());
        assertEquals("Integration", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:Integration)")
                        && cypher.contains("n.integrationId")
                        && cypher.contains("n.name")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForOpenQuestionType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("id", "OQ-001");
        row.put("nodeType", "OpenQuestion");
        row.put("displayName", "What approval evidence is minimally required before a draft agent can enter the publish flow?");
        row.put("status", "IN_DEFINITION");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 3L);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("open-question", null, null, "approval", 10);

        assertEquals(1, responses.size());
        assertEquals("OQ-001", responses.get(0).id());
        assertEquals("OpenQuestion", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:OpenQuestion)")
                        && cypher.contains("n.questionId")
                        && cypher.contains("n.question")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForChannelType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        var row = new java.util.LinkedHashMap<String, Object>();
        row.put("id", "CH-WEB-DSK");
        row.put("nodeType", "Channel");
        row.put("displayName", "Web Desktop");
        row.put("status", "");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 8L);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("channel", null, null, "web", 10);

        assertEquals(1, responses.size());
        assertEquals("CH-WEB-DSK", responses.get(0).id());
        assertEquals("Channel", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:Channel)")
                        && cypher.contains("n.channelCode")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForApplicationType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        var row = new java.util.LinkedHashMap<String, Object>();
        row.put("id", "APP-DH");
        row.put("nodeType", "Application");
        row.put("displayName", "Design Hub");
        row.put("status", "IN_IMPLEMENTATION");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 5L);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("application", null, null, "design", 10);

        assertEquals(1, responses.size());
        assertEquals("APP-DH", responses.get(0).id());
        assertEquals("Application", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:Application)")
                        && cypher.contains("n.applicationId")
                        && cypher.contains("n.name")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForAgentPolicyType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        var row = new java.util.LinkedHashMap<String, Object>();
        row.put("id", "POL-DH-AGENT-001");
        row.put("nodeType", "AgentPolicy");
        row.put("displayName", "Design Hub bounded automation policy");
        row.put("status", null);
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 5L);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("policy", null, null, "automation", 10);

        assertEquals(1, responses.size());
        assertEquals("POL-DH-AGENT-001", responses.get(0).id());
        assertEquals("AgentPolicy", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:AgentPolicy)")
                        && cypher.contains("n.policyId")
                        && cypher.contains("n.name")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForCodeAssetType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        var row = new java.util.LinkedHashMap<String, Object>();
        row.put("id", "CA-FE-DH-PAGE-001");
        row.put("nodeType", "CodeAsset");
        row.put("displayName", "src/app/features/design-hub/design-hub.page.ts");
        row.put("status", "IMPLEMENTED");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 3L);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("code-asset", null, null, "design-hub.page", 10);

        assertEquals(1, responses.size());
        assertEquals("CA-FE-DH-PAGE-001", responses.get(0).id());
        assertEquals("CodeAsset", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:CodeAsset)")
                        && cypher.contains("n.codeAssetId")
                        && cypher.contains("n.filePath")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForValidationRuleType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        var row = new java.util.LinkedHashMap<String, Object>();
        row.put("id", "VR-AUTH-001");
        row.put("nodeType", "ValidationRule");
        row.put("displayName", "Password must include upper, lower, number, and be at least 8 characters.");
        row.put("status", "DEFINED");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 2L);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("validation-rule", null, null, "password", 10);

        assertEquals(1, responses.size());
        assertEquals("VR-AUTH-001", responses.get(0).id());
        assertEquals("ValidationRule", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:ValidationRule)")
                        && cypher.contains("n.validationRuleId")
                        && cypher.contains("n.errorMessage")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnGraphObjectSummariesForProcessActivityType() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        var row = new java.util.LinkedHashMap<String, Object>();
        row.put("id", "ACT-PROC-SCREEN-REVIEW-001");
        row.put("nodeType", "ProcessActivity");
        row.put("displayName", "Review screen design");
        row.put("status", "DEFINED");
        row.put("module", null);
        row.put("domain", null);
        row.put("routePath", null);
        row.put("relationCount", 3L);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(row));

        List<GraphObjectSummaryResponse> responses = service.getObjects("process-activity", null, null, "review", 10);

        assertEquals(1, responses.size());
        assertEquals("ACT-PROC-SCREEN-REVIEW-001", responses.get(0).id());
        assertEquals("ProcessActivity", responses.get(0).nodeType());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:ProcessActivity)")
                        && cypher.contains("n.activityId")
                        && cypher.contains("n.name")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldExpandGraphObjectRelationsAndApplyNeighborLimit() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "root", Map.of(
                        "id", "SCR-AUTH",
                        "nodeType", "Screen",
                        "displayName", "Login / Sign In",
                        "status", "APPROVED",
                        "module", "core",
                        "domain", "auth",
                        "routePath", "/auth/login",
                        "relationCount", 5L
                ),
                "outgoing", List.of(
                        Map.of(
                                "relationType", "HAS_INTERACTION",
                                "direction", "OUTGOING",
                                "node", Map.of(
                                        "id", "INT-AUTH-001",
                                        "nodeType", "Interaction",
                                        "displayName", "Submit Credentials",
                                        "status", ""
                                )
                        ),
                        Map.of(
                                "relationType", "ACCESSIBLE_BY_ROLE",
                                "direction", "OUTGOING",
                                "node", Map.of(
                                        "id", "USER",
                                        "nodeType", "BusinessRole",
                                        "displayName", "User",
                                        "status", "APPROVED"
                                )
                        )
                ),
                "incoming", List.of(
                        Map.of(
                                "relationType", "DELIVERS",
                                "direction", "INCOMING",
                                "node", Map.of(
                                        "id", "US-AUTH-001",
                                        "nodeType", "UserStory",
                                        "displayName", "User can sign in",
                                        "status", "APPROVED"
                                )
                        )
                )
        )));

        GraphRelationExpansionResponse response = service.expandObject("screen", "SCR-AUTH", 1).orElseThrow();

        assertEquals("SCR-AUTH", response.root().id());
        assertEquals(1, response.outgoing().size());
        assertEquals("ACCESSIBLE_BY_ROLE", response.outgoing().get(0).relationType());
        assertEquals(1, response.incoming().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSupportEpicRelationExpansionWithStablePortfolioIdentifiers() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> root = new java.util.LinkedHashMap<>();
        root.put("id", "EPIC-AI-001");
        root.put("nodeType", "Epic");
        root.put("displayName", "Agent builder and orchestration");
        root.put("status", "IN_IMPLEMENTATION");
        root.put("module", null);
        root.put("domain", null);
        root.put("routePath", null);
        root.put("relationCount", 2L);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "root", root,
                "outgoing", List.of(Map.of(
                        "relationType", "HAS_FEATURE",
                        "direction", "OUTGOING",
                        "node", Map.of(
                                "id", "FEAT-AI",
                                "nodeType", "Feature",
                                "displayName", "Agent Builder",
                                "status", "IN_IMPLEMENTATION"
                        )
                )),
                "incoming", List.of(Map.of(
                        "relationType", "HAS_EPIC",
                        "direction", "INCOMING",
                        "node", Map.of(
                                "id", "PORT-DH-001",
                                "nodeType", "RequirementPortfolio",
                                "displayName", "Design Hub Delivery Portfolio",
                                "status", "IN_IMPLEMENTATION"
                        )
                ))
        )));

        GraphRelationExpansionResponse response = service.expandObject("epic", "EPIC-AI-001", 5).orElseThrow();

        assertEquals("EPIC-AI-001", response.root().id());
        assertEquals("PORT-DH-001", response.incoming().get(0).node().id());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:Epic {epicId: $id})")
                        && cypher.contains("toString(m.portfolioId)")
                        && cypher.contains("toString(m.epicId)")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSupportBusinessCapabilityRelationExpansionWithProcessIdentifiers() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        Map<String, Object> root = new java.util.LinkedHashMap<>();
        root.put("id", "CAP-SCREEN-MGMT");
        root.put("nodeType", "BusinessCapability");
        root.put("displayName", "Screen Management");
        root.put("status", "IN_IMPLEMENTATION");
        root.put("module", null);
        root.put("domain", null);
        root.put("routePath", null);
        root.put("relationCount", 3L);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "root", root,
                "outgoing", List.of(Map.of(
                        "relationType", "REALIZED_BY_PROCESS",
                        "direction", "OUTGOING",
                        "node", Map.of(
                                "id", "PROC-SCREEN-BUILD",
                                "nodeType", "BusinessProcess",
                                "displayName", "Screen Builder Process",
                                "status", "IN_IMPLEMENTATION"
                        )
                )),
                "incoming", List.of()
        )));

        GraphRelationExpansionResponse response = service.expandObject("capability", "CAP-SCREEN-MGMT", 5).orElseThrow();

        assertEquals("CAP-SCREEN-MGMT", response.root().id());
        assertEquals("PROC-SCREEN-BUILD", response.outgoing().get(0).node().id());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (n:BusinessCapability {capabilityId: $id})")
                        && cypher.contains("toString(m.processId)")
                        && cypher.contains("toString(m.capabilityId)")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnExternalArtifactSummaries() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(Map.ofEntries(
                Map.entry("externalId", "EXT-JIRA-001"),
                Map.entry("system", "JIRA"),
                Map.entry("externalType", "STORY"),
                Map.entry("key", "DH-101"),
                Map.entry("title", "User sign-in and session recovery"),
                Map.entry("projectScope", "Design Hub / Identity"),
                Map.entry("workflowState", "In Progress"),
                Map.entry("priority", "High"),
                Map.entry("owner", "Aisha Coleman"),
                Map.entry("reporter", "Marco Lane"),
                Map.entry("labels", List.of("design-hub", "story", "auth")),
                Map.entry("url", "https://jira.example.com/browse/DH-101"),
                Map.entry("syncStatus", "SYNCED"),
                Map.entry("lastSyncedAt", "2026-03-18T08:00Z"),
                Map.entry("status", "DEFINED"),
                Map.entry("representedObjectCount", 1L),
                Map.entry("childCount", 1L),
                Map.entry("dependencyCount", 1L),
                Map.entry("relatedCount", 1L)
        )));

        List<ExternalArtifactSummaryResponse> responses = service.getExternalArtifacts("JIRA", "SYNCED");

        assertEquals(1, responses.size());
        assertEquals("EXT-JIRA-001", responses.get(0).externalId());
        assertEquals(1L, responses.get(0).representedObjectCount());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (ea:ExternalArtifact)")
                        && cypher.contains("representedObjectCount")
                        && cypher.contains("dependencyCount")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapExternalArtifactTraversal() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.ofEntries(
                Map.entry("externalId", "EXT-JIRA-001"),
                Map.entry("system", "JIRA"),
                Map.entry("externalType", "STORY"),
                Map.entry("key", "DH-101"),
                Map.entry("title", "User sign-in and session recovery"),
                Map.entry("projectScope", "Design Hub / Identity"),
                Map.entry("workflowState", "In Progress"),
                Map.entry("priority", "High"),
                Map.entry("owner", "Aisha Coleman"),
                Map.entry("reporter", "Marco Lane"),
                Map.entry("labels", List.of("design-hub", "story", "auth")),
                Map.entry("customFields", List.of("area=Design Hub", "iteration=Sprint 24")),
                Map.entry("url", "https://jira.example.com/browse/DH-101"),
                Map.entry("syncStatus", "SYNCED"),
                Map.entry("lastSyncedAt", "2026-03-18T08:00Z"),
                Map.entry("status", "DEFINED"),
                Map.entry("parents", List.of(Map.of(
                        "externalId", "EXT-JIRA-EPIC-001",
                        "system", "JIRA",
                        "externalType", "EPIC",
                        "key", "DH-100",
                        "title", "Access and authentication hardening",
                        "workflowState", "In Progress",
                        "syncStatus", "SYNCED",
                        "status", "DEFINED"
                ))),
                Map.entry("children", List.of(Map.of(
                        "externalId", "EXT-JIRA-TASK-001",
                        "system", "JIRA",
                        "externalType", "TASK",
                        "key", "DH-102",
                        "title", "Implement sign-in telemetry and retry task",
                        "workflowState", "Selected for Development",
                        "syncStatus", "SYNCED",
                        "status", "DEFINED"
                ))),
                Map.entry("dependencies", List.of(Map.of(
                        "externalId", "EXT-AZDO-001",
                        "system", "AZURE_DEVOPS",
                        "externalType", "BUG",
                        "key", "AB#245",
                        "title", "Retry banner remains visible after successful login",
                        "workflowState", "Active",
                        "syncStatus", "SYNCED",
                        "status", "DEFINED"
                ))),
                Map.entry("relatedArtifacts", List.of()),
                Map.entry("duplicates", List.of()),
                Map.entry("representedObjects", List.of(Map.of(
                        "id", "US-AUTH-001",
                        "nodeType", "UserStory",
                        "displayName", "User can sign in",
                        "status", "APPROVED"
                )))
        )));

        ExternalArtifactTraversalResponse response = service.getExternalArtifact("EXT-JIRA-001").orElseThrow();

        assertEquals("EXT-JIRA-001", response.externalId());
        assertEquals(1, response.parents().size());
        assertEquals(1, response.children().size());
        assertEquals(1, response.dependencies().size());
        assertEquals("Sprint 24", response.customFields().get("iteration"));
        assertEquals(1, response.representedObjects().size());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (ea:ExternalArtifact {externalId: $externalId})")
                        && cypher.contains("customFields")
                        && cypher.contains("relatedArtifacts")
                        && cypher.contains("representedObjects")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapPersonaTraversal() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "personaId", "PER-ADMIN",
                "name", "Administrator",
                "summary", "Platform administrator",
                "status", "APPROVED",
                "roleKeys", List.of("ADMIN"),
                "journeys", List.of(Map.of(
                        "journeyId", "JRN-AUTH-01",
                        "title", "Authenticate",
                        "status", "APPROVED",
                        "stepCount", 3L,
                        "screenCount", 2L
                )),
                "roles", List.of(Map.of(
                        "id", "ADMIN",
                        "nodeType", "BusinessRole",
                        "displayName", "Administrator",
                        "status", "APPROVED"
                )),
                "channelReach", List.of(Map.of(
                        "id", "CH-WEB",
                        "nodeType", "Channel",
                        "displayName", "Web",
                        "status", ""
                )),
                "screenCount", 2L,
                "storyCount", 1L
        )));

        PersonaTraversalResponse response = service.getPersonaTraversal("PER-ADMIN").orElseThrow();

        assertEquals("PER-ADMIN", response.personaId());
        assertEquals(1, response.journeys().size());
        assertEquals("JRN-AUTH-01", response.journeys().get(0).journeyId());
        assertEquals(1, response.channelReach().size());
        assertEquals(1L, response.storyCount());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapPersonaSummaries() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(Map.of(
                "personaId", "PER-UX-007",
                "name", "Design Lead",
                "summary", "Owns the experience flow from exploration through delivery sign-off.",
                "status", "IDENTIFIED",
                "journeyCount", 2L,
                "screenCount", 7L,
                "storyCount", 4L,
                "channelCount", 2L
        )));

        List<PersonaSummaryResponse> responses = service.getPersonas("IDENTIFIED");

        assertEquals(1, responses.size());
        assertEquals("PER-UX-007", responses.get(0).personaId());
        assertEquals(2L, responses.get(0).journeyCount());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (p:Persona)")
                        && cypher.contains("count(DISTINCT j) AS journeyCount")
                        && cypher.contains("count(DISTINCT channel) AS channelCount")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapJourneyTraversalInOrder() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "journeyId", "JRN-AUTH-01",
                "title", "Authenticate",
                "goalStatement", "Sign in and reach the home screen",
                "status", "APPROVED",
                "persona", Map.of(
                        "id", "PER-ADMIN",
                        "nodeType", "Persona",
                        "displayName", "Administrator",
                        "status", "APPROVED"
                ),
                "steps", List.of(
                        Map.of(
                                "stepId", "STEP-02",
                                "label", "Submit credentials",
                                "orderIndex", 2,
                                "screen", Map.of(
                                        "id", "SCR-AUTH",
                                        "nodeType", "Screen",
                                        "displayName", "Login / Sign In",
                                        "status", "APPROVED"
                                ),
                                "touchpoint", Map.of(),
                                "interaction", Map.of(
                                        "id", "INT-AUTH-001",
                                        "nodeType", "Interaction",
                                        "displayName", "Submit Credentials",
                                        "status", ""
                                )
                        ),
                        Map.of(
                                "stepId", "STEP-01",
                                "label", "Open login page",
                                "orderIndex", 1,
                                "screen", Map.of(
                                        "id", "SCR-AUTH",
                                        "nodeType", "Screen",
                                        "displayName", "Login / Sign In",
                                        "status", "APPROVED"
                                ),
                                "touchpoint", Map.of(
                                        "id", "TP-WEB-ENTRY",
                                        "nodeType", "Touchpoint",
                                        "displayName", "Web Entry",
                                        "status", ""
                                ),
                                "interaction", Map.of()
                        )
                )
        )));

        JourneyTraversalResponse response = service.getJourneyTraversal("JRN-AUTH-01").orElseThrow();

        assertEquals("JRN-AUTH-01", response.journeyId());
        assertEquals("PER-ADMIN", response.persona().id());
        assertEquals(2, response.steps().size());
        assertEquals("STEP-01", response.steps().get(0).stepId());
        assertEquals("STEP-02", response.steps().get(1).stepId());
        assertTrue(response.steps().get(0).touchpoint() != null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapChannelSummaries() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(Map.of(
                "channelCode", "CH-WEB-DSK",
                "displayName", "Web Desktop",
                "channelType", "WEB",
                "touchpointCount", 8L,
                "screenCount", 6L
        )));

        List<ChannelSummaryResponse> responses = service.getChannels("WEB");

        assertEquals(1, responses.size());
        assertEquals("CH-WEB-DSK", responses.get(0).channelCode());
        assertEquals(8L, responses.get(0).touchpointCount());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (ch:Channel)")
                        && cypher.contains("count(DISTINCT tp) AS touchpointCount")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapChannelTraversal() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "channelCode", "CH-WEB-DSK",
                "displayName", "Web Desktop",
                "channelType", "WEB",
                "touchpoints", List.of(Map.of(
                        "touchpointId", "TP-GALLERY-MENU",
                        "label", "Gallery menu entry",
                        "surfaceId", "SCR-AGT-GALLERY",
                        "entryMechanisms", List.of("Left-nav Gallery menu item click"),
                        "roleKeys", List.of(),
                        "personaIds", List.of(),
                        "targetScreen", Map.of(
                                "id", "SCR-AGT-GALLERY",
                                "nodeType", "Screen",
                                "displayName", "Template Gallery",
                                "status", "IN_DEFINITION"
                        )
                )),
                "screens", List.of(Map.of(
                        "id", "SCR-AGT-GALLERY",
                        "nodeType", "Screen",
                        "displayName", "Template Gallery",
                        "status", "IN_DEFINITION"
                )),
                "coverageGaps", List.of(Map.of(
                        "touchpointId", "TP-ORPHAN",
                        "reason", "Touchpoint is not linked to a target screen"
                )),
                "personaReach", List.of(Map.of(
                        "id", "PER-UX-007",
                        "nodeType", "Persona",
                        "displayName", "PER-UX-007",
                        "status", "IDENTIFIED"
                ))
        )));

        ChannelTraversalResponse response = service.getChannelTraversal("CH-WEB-DSK").orElseThrow();

        assertEquals("CH-WEB-DSK", response.channelCode());
        assertEquals(1, response.touchpoints().size());
        assertEquals("TP-GALLERY-MENU", response.touchpoints().get(0).touchpointId());
        assertEquals("SCR-AGT-GALLERY", response.touchpoints().get(0).targetScreen().id());
        assertEquals(1, response.coverageGaps().size());
        assertEquals(1, response.personaReach().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapBusinessCapabilitySummaries() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(Map.of(
                "capabilityId", "CAP-SCREEN-MGMT",
                "name", "Screen Management",
                "domainCode", "DOM-DESIGN",
                "domainName", "Design Management",
                "processCount", 1L,
                "applicationCount", 1L,
                "featureCount", 1L,
                "organizationCount", 2L
        )));

        List<BusinessCapabilitySummaryResponse> responses = service.getBusinessCapabilities("DOM-DESIGN");

        assertEquals(1, responses.size());
        assertEquals("CAP-SCREEN-MGMT", responses.get(0).capabilityId());
        assertEquals(2L, responses.get(0).organizationCount());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (cap:BusinessCapability)")
                        && cypher.contains("Organization)-[:OWNS]->(application)")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapBusinessArchitectureTraversal() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "capabilityId", "CAP-SCREEN-MGMT",
                "name", "Screen Management",
                "description", "Manage screen inventory and review workflow",
                "status", "DEFINED",
                "domainCode", "DOM-DESIGN",
                "domainName", "Design Management",
                "processes", List.of(Map.of(
                        "id", "PROC-SCREEN-REVIEW",
                        "nodeType", "BusinessProcess",
                        "displayName", "Screen Review Process",
                        "status", "DEFINED"
                )),
                "applications", List.of(Map.of(
                        "id", "APP-DH",
                        "nodeType", "Application",
                        "displayName", "Design Hub",
                        "status", "IMPLEMENTED"
                )),
                "features", List.of(Map.of(
                        "id", "FEAT-AI",
                        "nodeType", "Feature",
                        "displayName", "Agent Builder",
                        "status", "IN_IMPLEMENTATION"
                )),
                "organizations", List.of(Map.of(
                        "id", "ORG-DH-PLATFORM",
                        "displayName", "Design Hub Platform Team",
                        "organizationType", "TEAM",
                        "status", "IMPLEMENTED"
                ))
        )));

        BusinessArchitectureResponse response = service.getBusinessArchitecture("CAP-SCREEN-MGMT").orElseThrow();

        assertEquals("CAP-SCREEN-MGMT", response.capabilityId());
        assertEquals("DOM-DESIGN", response.domainCode());
        assertEquals(1, response.processes().size());
        assertEquals("ORG-DH-PLATFORM", response.organizations().get(0).orgId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapApplicationSummaries() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(Map.of(
                "applicationId", "APP-DH",
                "name", "Design Hub",
                "applicationType", "WEB",
                "status", "IMPLEMENTED",
                "componentCount", 2L,
                "apiCount", 3L,
                "screenCount", 4L,
                "featureCount", 1L,
                "dependencyCount", 1L,
                "ownerNames", List.of("Design Hub Platform Team")
        )));

        List<ApplicationSummaryResponse> responses = service.getApplications("WEB");

        assertEquals(1, responses.size());
        assertEquals("APP-DH", responses.get(0).applicationId());
        assertEquals("WEB", responses.get(0).applicationType());
        assertEquals(1L, responses.get(0).dependencyCount());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (app:Application)")
                        && cypher.contains("DEPENDS_ON_COMPONENT")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapApplicationArchitectureTraversal() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.ofEntries(
                Map.entry("applicationId", "APP-DH"),
                Map.entry("name", "Design Hub"),
                Map.entry("description", "Implementation workbench for design delivery readiness"),
                Map.entry("applicationType", "WEB"),
                Map.entry("status", "IMPLEMENTED"),
                Map.entry("ownerNames", List.of("Design Hub Platform Team")),
                Map.entry("components", List.of(Map.of(
                        "componentId", "CMP-DH-BACKEND",
                        "name", "Design Hub Backend",
                        "componentType", "SERVICE",
                        "frameworkFamily", "Spring Boot",
                        "runtime", "Java 23",
                        "modulePath", "backend",
                        "status", "IMPLEMENTED",
                        "apis", List.of(Map.of(
                                "id", "API-POST-API-V1-AUTH-LOGIN",
                                "nodeType", "ApiContract",
                                "displayName", "POST /api/v1/auth/login",
                                "status", "APPROVED"
                        )),
                        "screens", List.of(Map.of(
                                "id", "SCR-AUTH-LOGIN",
                                "nodeType", "Screen",
                                "displayName", "Login / Sign In",
                                "status", "APPROVED"
                        )),
                        "dependencies", List.of(Map.of(
                                "id", "CMP-IDP-AUTH-API",
                                "nodeType", "ApplicationComponent",
                                "displayName", "Identity Platform Auth API",
                                "status", "IMPLEMENTED"
                        ))
                ))),
                Map.entry("apis", List.of(Map.of(
                        "id", "API-POST-API-V1-AUTH-LOGIN",
                        "nodeType", "ApiContract",
                        "displayName", "POST /api/v1/auth/login",
                        "status", "APPROVED"
                ))),
                Map.entry("screens", List.of(Map.of(
                        "id", "SCR-AUTH-LOGIN",
                        "nodeType", "Screen",
                        "displayName", "Login / Sign In",
                        "status", "APPROVED"
                ))),
                Map.entry("features", List.of(Map.of(
                        "id", "FEAT-AI",
                        "nodeType", "Feature",
                        "displayName", "Agent Builder",
                        "status", "IN_IMPLEMENTATION"
                ))),
                Map.entry("dependencies", List.of(Map.of(
                        "applicationId", "APP-IDP",
                        "name", "Identity Platform",
                        "direction", "OUTBOUND",
                        "status", "IMPLEMENTED"
                )))
        )));

        ApplicationArchitectureResponse response = service.getApplicationArchitecture("APP-DH").orElseThrow();

        assertEquals("APP-DH", response.applicationId());
        assertEquals("WEB", response.applicationType());
        assertEquals(1, response.components().size());
        assertEquals("CMP-DH-BACKEND", response.components().get(0).componentId());
        assertEquals("APP-IDP", response.dependencies().get(0).applicationId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapDataObjectSummaries() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(Map.of(
                "objectId", "BO-AGENT-CONFIG",
                "name", "Agent Configuration",
                "domain", "AI_DESIGN",
                "sensitivity", "INTERNAL",
                "status", "DEFINED",
                "mappedEntityCount", 1L,
                "flowCount", 2L,
                "apiCount", 2L,
                "screenCount", 3L
        )));

        List<DataArchitectureObjectSummaryResponse> responses = service.getDataObjects("AI_DESIGN");

        assertEquals(1, responses.size());
        assertEquals("BO-AGENT-CONFIG", responses.get(0).objectId());
        assertEquals(2L, responses.get(0).flowCount());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (obj:BusinessObject)")
                        && cypher.contains("MAPPED_TO")
                        && cypher.contains("CARRIES")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapDataArchitectureTraversal() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.ofEntries(
                Map.entry("objectId", "BO-AGENT-CONFIG"),
                Map.entry("name", "Agent Configuration"),
                Map.entry("domain", "AI_DESIGN"),
                Map.entry("description", "Business-level representation of an agent definition and its authoring metadata."),
                Map.entry("sensitivity", "INTERNAL"),
                Map.entry("status", "DEFINED"),
                Map.entry("entities", List.of(Map.of(
                        "entityId", "DE-AGENT",
                        "name", "Agent",
                        "entityType", "CONFIGURATION",
                        "fieldCount", 1L,
                        "status", "DEFINED"
                ))),
                Map.entry("flows", List.of(Map.of(
                        "flowId", "FLOW-AGENT-DRAFT",
                        "name", "Agent draft persistence flow",
                        "direction", "OUTBOUND",
                        "status", "DEFINED",
                        "sourceApplicationId", "APP-DH",
                        "sourceApplicationName", "Design Hub",
                        "targetApplicationId", "APP-DH",
                        "targetApplicationName", "Design Hub"
                ))),
                Map.entry("apis", List.of(Map.of(
                        "id", "API-PUT-API-V1-AGENTS-ID-DRAFT",
                        "nodeType", "ApiContract",
                        "displayName", "PUT /api/v1/agents/{id}/draft",
                        "status", "DEFINED"
                ))),
                Map.entry("screens", List.of(Map.of(
                        "id", "SCR-AGT-BUILDER",
                        "nodeType", "Screen",
                        "displayName", "Agent Builder (3-panel)",
                        "status", "IN_DEFINITION"
                ))),
                Map.entry("children", List.of(Map.of(
                        "id", "BO-AGENT-PUBLISH-REQ",
                        "nodeType", "BusinessObject",
                        "displayName", "Agent Publish Request",
                        "status", "DEFINED"
                )))
        )));

        DataArchitectureResponse response = service.getDataArchitecture("BO-AGENT-CONFIG").orElseThrow();

        assertEquals("BO-AGENT-CONFIG", response.objectId());
        assertEquals(1, response.entities().size());
        assertEquals("DE-AGENT", response.entities().get(0).entityId());
        assertEquals("FLOW-AGENT-DRAFT", response.flows().get(0).flowId());
        assertEquals("BO-AGENT-PUBLISH-REQ", response.children().get(0).id());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapInfrastructureDeploymentSummaries() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(Map.of(
                "deploymentId", "DEP-DEV-001",
                "name", "Design Hub Dev Stack",
                "environment", "DEV",
                "status", "IMPLEMENTED",
                "componentCount", 2L,
                "applicationCount", 1L,
                "infrastructureCount", 1L
        )));

        List<InfrastructureDeploymentSummaryResponse> responses = service.getInfrastructureDeployments("DEV");

        assertEquals(1, responses.size());
        assertEquals("DEP-DEV-001", responses.get(0).deploymentId());
        assertEquals(2L, responses.get(0).componentCount());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (dep:Deployment)")
                        && cypher.contains("HOSTS")
                        && cypher.contains("DEPLOYED_ON")
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapInfrastructureArchitectureTraversal() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.ofEntries(
                Map.entry("deploymentId", "DEP-DEV-001"),
                Map.entry("name", "Design Hub Dev Stack"),
                Map.entry("environment", "DEV"),
                Map.entry("description", "Development deployment topology for the Design Hub frontend and backend."),
                Map.entry("status", "IMPLEMENTED"),
                Map.entry("components", List.of(Map.of(
                        "id", "CMP-DH-FRONTEND",
                        "nodeType", "ApplicationComponent",
                        "displayName", "Design Hub Frontend",
                        "status", "IMPLEMENTED"
                ))),
                Map.entry("infrastructureNodes", List.of(Map.of(
                        "nodeId", "INF-AKS-DEV-001",
                        "name", "EMSIST Platform Dev Cluster",
                        "nodeType", "KUBERNETES_CLUSTER",
                        "location", "Azure UAE North",
                        "status", "IMPLEMENTED"
                ))),
                Map.entry("applications", List.of(Map.of(
                        "id", "APP-DH",
                        "nodeType", "Application",
                        "displayName", "Design Hub",
                        "status", "IMPLEMENTED"
                ))),
                Map.entry("elements", List.of())
        )));

        InfrastructureArchitectureResponse response = service.getInfrastructureArchitecture("DEP-DEV-001").orElseThrow();

        assertEquals("DEP-DEV-001", response.deploymentId());
        assertEquals(1, response.components().size());
        assertEquals("INF-AKS-DEV-001", response.infrastructureNodes().get(0).nodeId());
        assertEquals("APP-DH", response.applications().get(0).id());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapStoryTraceabilityAndHighlightMissingUpperSpine() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.ofEntries(
                Map.entry("objective", Map.of()),
                Map.entry("portfolio", Map.of()),
                Map.entry("epic", Map.of()),
                Map.entry("feature", Map.of()),
                Map.entry("story", Map.of(
                        "id", "US-AI-090",
                        "nodeType", "UserStory",
                        "displayName", "Builder canvas interactions ready for agent composition",
                        "status", "DEFINED"
                )),
                Map.entry("screens", List.of(Map.of(
                        "id", "SCR-AGT-BUILDER",
                        "nodeType", "Screen",
                        "displayName", "Agent Builder (3-panel)",
                        "status", "IN_DEFINITION"
                ))),
                Map.entry("interactions", List.of(Map.of(
                        "id", "INT-R05-BUILDER-001",
                        "nodeType", "Interaction",
                        "displayName", "Component from palette",
                        "status", ""
                ))),
                Map.entry("apis", List.of(Map.of(
                        "id", "API-AGT-001",
                        "nodeType", "ApiContract",
                        "displayName", "POST /api/agents/run",
                        "status", "APPROVED"
                ))),
                Map.entry("dataEntities", List.of(Map.of())),
                Map.entry("messages", List.of(Map.of())),
                Map.entry("tasks", List.of(Map.of(
                        "id", "TASK-AI-001",
                        "nodeType", "Task",
                        "displayName", "Implement builder canvas interactions",
                        "status", "IN_IMPLEMENTATION"
                )))
        )));

        TraceabilityStoryResponse response = service.getStoryTraceability("US-AI-090").orElseThrow();

        assertEquals("US-AI-090", response.story().id());
        assertEquals(1, response.screens().size());
        assertEquals(1, response.apis().size());
        assertEquals(1, response.tasks().size());
        assertEquals(List.of("BusinessObjective", "RequirementPortfolio", "Epic", "Feature"), response.missingSpineSegments());
    }
}
