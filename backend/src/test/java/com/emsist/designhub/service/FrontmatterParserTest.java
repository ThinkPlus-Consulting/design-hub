package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FrontmatterParserTest {

    private final FrontmatterParser parser = new FrontmatterParser();

    @Test
    void shouldParseValidFrontmatter() {
        String markdown = """
                ---
                id: US-SCR-042
                type: UserStory
                status: DEFINED
                version: 1
                ---
                # User Story
                As a user, I want to see the screen.
                """;

        var result = parser.parse(markdown);

        assertTrue(result.isPresent());
        var fm = result.get();
        assertEquals("US-SCR-042", fm.getId());
        assertEquals("UserStory", fm.getType());
        assertEquals("DEFINED", fm.getStatus());
        assertEquals(1, fm.getVersion());
    }

    @Test
    void shouldReturnEmptyForMissingFrontmatter() {
        String markdown = "# No frontmatter\nJust content.";
        var result = parser.parse(markdown);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyForMalformedYaml() {
        String markdown = """
                ---
                id: [invalid yaml
                ---
                # Content
                """;
        var result = parser.parse(markdown);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldExtractBodyAfterFrontmatter() {
        String markdown = """
                ---
                id: US-SCR-042
                type: UserStory
                status: DEFINED
                version: 1
                ---
                # Description
                Body text here.
                """;

        var body = parser.extractBody(markdown);
        assertTrue(body.contains("# Description"));
        assertTrue(body.contains("Body text here."));
        assertFalse(body.contains("id: US-SCR-042"));
    }

    @Test
    void shouldParseReferencesFromFrontmatter() {
        String markdown = """
                ---
                id: US-SCR-042
                type: UserStory
                status: DEFINED
                version: 1
                delivers:
                  - SCR-SETTINGS-01
                  - API-SETTINGS-01
                verifiedBy:
                  - TC-SCR-042-01
                ---
                # Content
                """;

        var result = parser.parse(markdown);
        assertTrue(result.isPresent());
        var fm = result.get();
        assertEquals(2, fm.getDelivers().size());
        assertEquals(1, fm.getVerifiedBy().size());
    }
}
