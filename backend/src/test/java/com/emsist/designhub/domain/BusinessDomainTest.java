package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessDomainTest {

    @Test
    void shouldBuildBusinessDomainWithRequiredFields() {
        BusinessDomain domain = BusinessDomain.builder()
                .domainCode("DOM-DESIGN")
                .name("Design Management")
                .description("Manages design artifacts and flows")
                .activeStatus("ACTIVE")
                .build();

        assertEquals("DOM-DESIGN", domain.getDomainCode());
        assertEquals("Design Management", domain.getName());
        assertEquals("ACTIVE", domain.getActiveStatus());
    }

    @Test
    void shouldFollowDomainCodePattern() {
        BusinessDomain domain = BusinessDomain.builder()
                .domainCode("DOM-CORE")
                .name("Core Platform")
                .activeStatus("ACTIVE")
                .build();

        assertTrue(domain.getDomainCode().startsWith("DOM-"),
                "domainCode must follow pattern DOM-{code}");
    }

    @Test
    void shouldHoldCapabilities() {
        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-SCREEN-MGMT")
                .name("Screen Management")
                .status(Status.DEFINED)
                .build();

        BusinessDomain domain = BusinessDomain.builder()
                .domainCode("DOM-DESIGN")
                .name("Design Management")
                .activeStatus("ACTIVE")
                .capabilities(List.of(cap))
                .build();

        assertEquals(1, domain.getCapabilities().size());
        assertEquals("CAP-SCREEN-MGMT", domain.getCapabilities().get(0).getCapabilityId());
    }
}
