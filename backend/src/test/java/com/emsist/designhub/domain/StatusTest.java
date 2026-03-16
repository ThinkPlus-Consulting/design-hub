package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void shouldHaveExactlyTenValues() {
        assertEquals(10, Status.values().length);
    }

    @Test
    void shouldContainAllCanonicalValues() {
        assertNotNull(Status.valueOf("IDENTIFIED"));
        assertNotNull(Status.valueOf("IN_DEFINITION"));
        assertNotNull(Status.valueOf("DEFINED"));
        assertNotNull(Status.valueOf("IN_REVIEW"));
        assertNotNull(Status.valueOf("APPROVED"));
        assertNotNull(Status.valueOf("IN_IMPLEMENTATION"));
        assertNotNull(Status.valueOf("IMPLEMENTED"));
        assertNotNull(Status.valueOf("VERIFIED"));
        assertNotNull(Status.valueOf("DEPRECATED"));
        assertNotNull(Status.valueOf("RETIRED"));
    }

    @Test
    void shouldMaintainCanonicalOrder() {
        Status[] values = Status.values();
        assertEquals(Status.IDENTIFIED, values[0]);
        assertEquals(Status.IN_DEFINITION, values[1]);
        assertEquals(Status.DEFINED, values[2]);
        assertEquals(Status.IN_REVIEW, values[3]);
        assertEquals(Status.APPROVED, values[4]);
        assertEquals(Status.IN_IMPLEMENTATION, values[5]);
        assertEquals(Status.IMPLEMENTED, values[6]);
        assertEquals(Status.VERIFIED, values[7]);
        assertEquals(Status.DEPRECATED, values[8]);
        assertEquals(Status.RETIRED, values[9]);
    }
}
