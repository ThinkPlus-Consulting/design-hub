package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequirementSyncServiceTest {

    private final RequirementSyncService service = new RequirementSyncService();

    @Test
    void shouldComputeContentHashForDocAuthoredFields() {
        String hash = service.computeContentHash("US-AUTH-001",
                "As an admin, I want to manage users",
                "Given I am logged in as admin, When I navigate to users...");

        assertNotNull(hash);
        assertTrue(hash.startsWith("sha256:"));
        assertEquals(71, hash.length()); // "sha256:" (7) + 64 hex chars
    }

    @Test
    void shouldDetectDriftWhenHashesDiffer() {
        String hash1 = service.computeContentHash("US-AUTH-001",
                "As an admin, I want to manage users", "Given...");
        String hash2 = service.computeContentHash("US-AUTH-001",
                "As an admin, I want to manage users AND roles", "Given...");

        assertNotEquals(hash1, hash2);
        assertTrue(service.hasDrift(hash1, hash2));
    }

    @Test
    void shouldNotDetectDriftWhenHashesMatch() {
        String hash1 = service.computeContentHash("US-AUTH-001",
                "As an admin, I want to manage users", "Given...");
        String hash2 = service.computeContentHash("US-AUTH-001",
                "As an admin, I want to manage users", "Given...");

        assertEquals(hash1, hash2);
        assertFalse(service.hasDrift(hash1, hash2));
    }
}
