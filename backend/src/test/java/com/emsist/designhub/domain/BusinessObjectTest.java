package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessObjectTest {

    @Test
    void shouldBuildBusinessObjectWithStubFields() {
        BusinessObject businessObject = BusinessObject.builder()
                .objectId("BOB-CASE-001")
                .name("Case")
                .status(Status.DEFINED)
                .build();

        assertEquals("BOB-CASE-001", businessObject.getObjectId());
        assertEquals("Case", businessObject.getName());
        assertEquals(Status.DEFINED, businessObject.getStatus());
    }

    @Test
    void shouldFollowBusinessObjectIdPattern() {
        BusinessObject businessObject = BusinessObject.builder()
                .objectId("BOB-CUSTOMER-002")
                .name("Customer")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(businessObject.getObjectId().startsWith("BOB-"),
                "objectId must follow pattern BOB-{domain}-{seq}");
    }
}
