package com.emsist.designhub.service;

import com.emsist.designhub.domain.TargetKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    @Mock private Neo4jClient neo4jClient;

    @InjectMocks
    private AssessmentService service;

    // --- Resolver tests (pure logic, no Neo4j) ---

    @Test
    void shouldResolveTargetLabel() {
        assertEquals("BusinessCapability", service.resolveTargetLabel(TargetKind.CAP));
        assertEquals("BusinessProcess", service.resolveTargetLabel(TargetKind.PROC));
        assertEquals("ProcessActivity", service.resolveTargetLabel(TargetKind.ACT));
        assertEquals("Application", service.resolveTargetLabel(TargetKind.APP));
        assertEquals("ApplicationComponent", service.resolveTargetLabel(TargetKind.CMP));
        assertEquals("ApiContract", service.resolveTargetLabel(TargetKind.API));
        assertEquals("DataEntity", service.resolveTargetLabel(TargetKind.DE));
    }

    @Test
    void shouldResolveTargetIdField() {
        assertEquals("capabilityId", service.resolveTargetIdField(TargetKind.CAP));
        assertEquals("processId", service.resolveTargetIdField(TargetKind.PROC));
        assertEquals("activityId", service.resolveTargetIdField(TargetKind.ACT));
        assertEquals("applicationId", service.resolveTargetIdField(TargetKind.APP));
        assertEquals("componentId", service.resolveTargetIdField(TargetKind.CMP));
        assertEquals("contractId", service.resolveTargetIdField(TargetKind.API));
        assertEquals("entityId", service.resolveTargetIdField(TargetKind.DE));
    }

    // --- createAssessesEdge (executes Neo4jClient) ---

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreateAssessesEdgeViaNeo4jClient() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.run()).thenReturn(null);

        service.createAssessesEdge("ASSESS-CAP-001", TargetKind.CAP, "CAP-AUTH");

        // Verify Neo4jClient was called with a query containing ASSESSES
        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (a)-[:ASSESSES]->(target)")
                && ((String) cypher).contains("Assessment")));
        // Verify 2 bind calls: assessmentId, targetId (label/idField injected via String.formatted)
        verify(runnableSpec, atLeast(2)).bind(any());
        verify(runnableSpec).run();
    }

    // --- findAssessmentsForTarget (executes Neo4jClient) ---

    @Test
    @SuppressWarnings("unchecked")
    void shouldFindAssessmentsForTargetViaNeo4jClient() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(anyString())).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((Collection) List.of(
                Map.of("assessmentId", "ASSESS-CAP-001", "name", "Auth Maturity")));

        var results = service.findAssessmentsForTarget(TargetKind.CAP, "CAP-AUTH");

        assertEquals(1, results.size());
        assertEquals("ASSESS-CAP-001", results.get(0).get("assessmentId"));
        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ASSESSES") && ((String) cypher).contains("RETURN")));
    }

    // --- findAssessmentsForTarget returns empty when no edges exist ---

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnEmptyWhenNoAssessmentsExist() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(anyString())).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((Collection) List.of());

        var results = service.findAssessmentsForTarget(TargetKind.APP, "APP-DH");

        assertTrue(results.isEmpty());
    }
}
