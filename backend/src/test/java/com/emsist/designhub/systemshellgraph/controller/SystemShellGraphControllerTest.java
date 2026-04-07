package com.emsist.designhub.systemshellgraph.controller;

import com.emsist.designhub.systemshellgraph.dto.ComponentRegistryDefinitionResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphValidationResponse;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphComponentRegistryService;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphIssueRegistryService;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphQueryService;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphSeedService;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SystemShellGraphControllerTest {

    private SystemShellGraphQueryService queryService;
    private SystemShellGraphComponentRegistryService componentRegistryService;
    private SystemShellGraphIssueRegistryService issueRegistryService;
    private SystemShellGraphSeedService seedService;
    private SystemShellGraphValidationService validationService;
    private SystemShellGraphController controller;

    @BeforeEach
    void setUp() {
        queryService = mock(SystemShellGraphQueryService.class);
        componentRegistryService = mock(SystemShellGraphComponentRegistryService.class);
        issueRegistryService = mock(SystemShellGraphIssueRegistryService.class);
        seedService = mock(SystemShellGraphSeedService.class);
        validationService = mock(SystemShellGraphValidationService.class);
        controller = new SystemShellGraphController(
                queryService,
                componentRegistryService,
                issueRegistryService,
                seedService,
                validationService
        );
    }

    @Test
    void getGraphReturnsSystemShellGraphPayload() {
        SystemShellGraphResponse response = new SystemShellGraphResponse(
                "SYSTEM_FRONTEND_GRAPH",
                "SYSTEM_FRONTEND_GRAPH_V1",
                "Frontend System Graph",
                List.of(),
                List.of()
        );
        when(queryService.getGraph()).thenReturn(response);

        var entity = controller.getGraph();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isSameAs(response);
        verify(queryService).getGraph();
    }

    @Test
    void reseedGraphTriggersSeedAndReturnsValidation() {
        SystemShellGraphValidationResponse validation = new SystemShellGraphValidationResponse(
                "SYSTEM_FRONTEND_GRAPH",
                "SYSTEM_FRONTEND_GRAPH_V1",
                true,
                0,
                List.of()
        );
        when(validationService.validateLiveGraph()).thenReturn(validation);

        var entity = controller.reseedGraph();

        verify(seedService).reseedCurrentScope();
        verify(validationService).validateLiveGraph();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isSameAs(validation);
    }

    @Test
    void getComponentRegistryDelegatesToRegistryService() {
        List<ComponentRegistryDefinitionResponse> definitions = List.of(
                new ComponentRegistryDefinitionResponse(
                        "CMP01",
                        "Component",
                        "button",
                        "Primary Button",
                        "Primary action component",
                        "cmp-01",
                        "draft",
                        "frontend/component.ts",
                        "CMP01.INSTANCE"
                )
        );
        when(componentRegistryService.getDefinitions()).thenReturn(definitions);

        var entity = controller.getComponentRegistry();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isEqualTo(definitions);
        verify(componentRegistryService).getDefinitions();
    }
}
