package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrganizationTest {

    @Test
    void shouldBuildOrganizationWithStubFields() {
        Organization organization = Organization.builder()
                .orgId("ORG-001")
                .name("Design Hub Team")
                .status(Status.DEFINED)
                .build();

        assertEquals("ORG-001", organization.getOrgId());
        assertEquals("Design Hub Team", organization.getName());
        assertEquals(Status.DEFINED, organization.getStatus());
    }

    @Test
    void shouldFollowOrganizationIdPattern() {
        Organization organization = Organization.builder()
                .orgId("ORG-014")
                .name("Architecture CoE")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(organization.getOrgId().startsWith("ORG-"),
                "orgId must follow pattern ORG-{seq}");
    }
}
