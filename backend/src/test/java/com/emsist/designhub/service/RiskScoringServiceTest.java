package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RiskScoringServiceTest {

    private final RiskScoringService scorer = new RiskScoringService();

    @Test
    void shouldScoreLowRiskForSmallChange() {
        var factors = new RiskScoringService.RiskFactors(
                2,     // blastRadius — 2 files affected
                false, // crossServiceImpact
                false, // dataModelChange
                false, // apiContractChange
                false, // securitySensitive
                false  // firstTimeFile
        );
        var result = scorer.score(factors);
        assertEquals("LOW", result.getLevel());
        assertTrue(result.getScore() <= 5);
    }

    @Test
    void shouldScoreCriticalForSecuritySensitiveDataModelCrossService() {
        var factors = new RiskScoringService.RiskFactors(
                15,    // blastRadius — 15 files
                true,  // crossServiceImpact
                true,  // dataModelChange
                true,  // apiContractChange
                true,  // securitySensitive
                false
        );
        var result = scorer.score(factors);
        assertEquals("CRITICAL", result.getLevel());
        assertTrue(result.getScore() >= 21);
    }

    @Test
    void shouldScoreMediumForModerateChange() {
        var factors = new RiskScoringService.RiskFactors(
                5,     // blastRadius — 5 files
                false,
                true,  // dataModelChange
                false,
                false,
                true   // firstTimeFile
        );
        var result = scorer.score(factors);
        assertTrue(result.getScore() >= 6 && result.getScore() <= 12);
        assertEquals("MEDIUM", result.getLevel());
    }
}
