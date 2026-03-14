package com.emsist.designhub.controller;

import com.emsist.designhub.dto.LintResult;
import com.emsist.designhub.service.RequirementLinterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LintControllerTest {

    @Mock
    private RequirementLinterService linterService;

    @InjectMocks
    private LintController controller;

    @Test
    void shouldReturnLintResult() {
        var expected = LintResult.builder()
                .file("test.md").artifactId("US-SCR-001").artifactType("UserStory")
                .errors(List.of()).warnings(List.of()).build();
        when(linterService.lint(anyString(), anyString())).thenReturn(expected);

        var request = new LintController.LintRequest("# content", "test.md");
        var response = controller.lint(request);

        assertEquals("US-SCR-001", response.getBody().getArtifactId());
        assertFalse(response.getBody().hasBlockingErrors());
    }
}
