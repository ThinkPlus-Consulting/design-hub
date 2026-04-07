package com.emsist.designhub.service;

import com.emsist.designhub.dto.RoleResponse;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock private Neo4jClient neo4jClient;

    @InjectMocks
    private RoleService roleService;

    @Test
    void shouldReturnBothBusinessRoleAndValidationRole() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);

        // Simulate two rows: one BusinessRole, one ValidationRole
        Collection<Map<String, Object>> rows = List.of(
                Map.of("roleKey", "ADMIN", "displayName", "Administrator",
                        "roleGroup", "tenant", "copyRestricted", false, "sortOrder", 1,
                        "screenCount", 5L, "touchpointCount", 2L,
                        "interactionCount", 3L, "journeyCount", 1L),
                Map.of("roleKey", "HITL_REVIEWER", "displayName", "HITL Reviewer",
                        "roleGroup", "review", "copyRestricted", false,
                        "screenCount", 1L, "touchpointCount", 0L,
                        "interactionCount", 0L, "journeyCount", 0L)
        );
        when(spec.fetch().all()).thenReturn(rows);

        List<RoleResponse> results = roleService.getAll();

        assertEquals(2, results.size());

        RoleResponse admin = results.stream()
                .filter(r -> "ADMIN".equals(r.roleKey())).findFirst().orElseThrow();
        assertEquals("Administrator", admin.displayName());
        assertEquals("tenant", admin.roleGroup());
        assertFalse(admin.copyRestricted());
        assertEquals(1, admin.sortOrder());

        RoleResponse hitl = results.stream()
                .filter(r -> "HITL_REVIEWER".equals(r.roleKey())).findFirst().orElseThrow();
        assertEquals("HITL Reviewer", hitl.displayName());
        assertEquals("review", hitl.roleGroup());  // scope mapped to roleGroup
        assertFalse(hitl.copyRestricted());
    }

    @Test
    void shouldQueryWithUnionAll() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.fetch().all()).thenReturn(List.of());

        roleService.getAll();

        // Verify the query includes UNION ALL for both node types
        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("BusinessRole")
                && ((String) cypher).contains("UNION ALL")
                && ((String) cypher).contains("ValidationRole")));
    }
}
