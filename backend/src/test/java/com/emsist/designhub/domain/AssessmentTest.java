package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AssessmentTest {

    @Test
    void shouldBuildAssessmentWithRequiredFields() {
        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-CAP-001")
                .name("Auth Capability Maturity")
                .assessmentType(AssessmentType.CAPABILITY)
                .targetKind(TargetKind.CAP)
                .assessmentDate(LocalDate.of(2026, 3, 15))
                .assessor("arch-agent")
                .status(Status.DEFINED)
                .build();

        assertEquals("ASSESS-CAP-001", assessment.getAssessmentId());
        assertEquals(AssessmentType.CAPABILITY, assessment.getAssessmentType());
        assertEquals(TargetKind.CAP, assessment.getTargetKind());
        assertEquals("arch-agent", assessment.getAssessor());
        assertEquals(Status.DEFINED, assessment.getStatus());
    }

    @Test
    void shouldBuildAssessmentWithOptionalFields() {
        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-APP-001")
                .name("Design Hub App Health")
                .assessmentType(AssessmentType.APPLICATION)
                .targetKind(TargetKind.APP)
                .assessmentDate(LocalDate.of(2026, 3, 15))
                .assessor("sa-agent")
                .maturityLevel(MaturityLevel.DEVELOPING)
                .currentStateDescription("11 entities, 14 edges")
                .targetStateDescription("75 nodes, 106 edges")
                .score(13)
                .status(Status.IN_REVIEW)
                .build();

        assertEquals(MaturityLevel.DEVELOPING, assessment.getMaturityLevel());
        assertEquals(13, assessment.getScore());
    }

    @Test
    void shouldSupportAllTargetKindValues() {
        assertEquals(7, TargetKind.values().length);
        assertNotNull(TargetKind.valueOf("CAP"));
        assertNotNull(TargetKind.valueOf("PROC"));
        assertNotNull(TargetKind.valueOf("ACT"));
        assertNotNull(TargetKind.valueOf("APP"));
        assertNotNull(TargetKind.valueOf("CMP"));
        assertNotNull(TargetKind.valueOf("API"));
        assertNotNull(TargetKind.valueOf("DE"));
    }

    @Test
    void shouldAttachGapsViaIdentifiesGap() {
        Gap gap = Gap.builder()
                .gapId("GAP-CAP-AUTH-001")
                .gapType("CAPABILITY_GAP")
                .severity("HIGH")
                .description("No MFA capability")
                .status(Status.IDENTIFIED)
                .build();

        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-CAP-002")
                .name("Auth Gap Assessment")
                .assessmentType(AssessmentType.CAPABILITY)
                .targetKind(TargetKind.CAP)
                .assessmentDate(LocalDate.of(2026, 3, 15))
                .assessor("arch-agent")
                .status(Status.DEFINED)
                .identifiedGaps(List.of(gap))
                .build();

        assertEquals(1, assessment.getIdentifiedGaps().size());
        assertEquals("CAPABILITY_GAP", assessment.getIdentifiedGaps().get(0).getGapType());
    }

    @Test
    void shouldSupportSecurityAssessmentWithApiTarget() {
        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-API-001")
                .name("Auth API Security Review")
                .assessmentType(AssessmentType.SECURITY)
                .targetKind(TargetKind.API)
                .assessmentDate(LocalDate.of(2026, 3, 15))
                .assessor("sec-agent")
                .status(Status.IN_REVIEW)
                .build();

        assertEquals(AssessmentType.SECURITY, assessment.getAssessmentType());
        assertEquals(TargetKind.API, assessment.getTargetKind());
    }
}
