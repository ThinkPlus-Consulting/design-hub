package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QualityConstraintTest {

    @Test
    void shouldBuildQualityConstraintWithRequiredFields() {
        QualityConstraint qc = QualityConstraint.builder()
                .constraintId("QC-PERF-001")
                .name("Screen load time < 2s")
                .constraintType("PERFORMANCE")
                .threshold("< 2000ms")
                .status(Status.DEFINED)
                .build();

        assertEquals("QC-PERF-001", qc.getConstraintId());
        assertEquals("PERFORMANCE", qc.getConstraintType());
        assertEquals("< 2000ms", qc.getThreshold());
    }

    @Test
    void shouldSupportAccessibilityConstraint() {
        QualityConstraint qc = QualityConstraint.builder()
                .constraintId("QC-A11Y-001")
                .name("WCAG AAA compliance")
                .constraintType("ACCESSIBILITY")
                .threshold("WCAG AAA")
                .measurementMethod("axe-core WCAG audit")
                .priority("CRITICAL")
                .status(Status.APPROVED)
                .build();

        assertEquals("ACCESSIBILITY", qc.getConstraintType());
        assertEquals("axe-core WCAG audit", qc.getMeasurementMethod());
        assertEquals("CRITICAL", qc.getPriority());
    }
}
