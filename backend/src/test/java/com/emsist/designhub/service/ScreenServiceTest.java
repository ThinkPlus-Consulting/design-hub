package com.emsist.designhub.service;

import com.emsist.designhub.repository.BusinessRoleRepository;
import com.emsist.designhub.repository.ScreenRepository;
import com.emsist.designhub.repository.UserStoryRepository;
import com.emsist.designhub.repository.ValidationRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScreenServiceTest {

    @Mock
    private ScreenRepository screenRepository;

    @Mock
    private BusinessRoleRepository businessRoleRepository;

    @Mock
    private ValidationRoleRepository validationRoleRepository;

    @Mock
    private UserStoryRepository userStoryRepository;

    @InjectMocks
    private ScreenService screenService;

    @Test
    void shouldIncludeBusinessAndValidationRolesInStats() {
        when(screenRepository.count()).thenReturn(66L);
        when(screenRepository.countByDesignStatus("COMPLETE")).thenReturn(15L);
        when(screenRepository.countByDesignStatus("SPECIFIED")).thenReturn(51L);
        when(screenRepository.countByDesignStatus("NOT_STARTED")).thenReturn(0L);
        when(screenRepository.countByDeliveryStatus("INTEGRATED")).thenReturn(13L);
        when(screenRepository.countByDeliveryStatus("TESTED")).thenReturn(0L);
        when(businessRoleRepository.count()).thenReturn(6L);
        when(validationRoleRepository.count()).thenReturn(2L);
        when(userStoryRepository.count()).thenReturn(139L);

        Map<String, Object> stats = screenService.getStats();

        assertEquals(66L, stats.get("totalScreens"));
        assertEquals(15L, stats.get("designComplete"));
        assertEquals(51L, stats.get("designSpecified"));
        assertEquals(0L, stats.get("designNotStarted"));
        assertEquals(13L, stats.get("deliveryIntegrated"));
        assertEquals(0L, stats.get("deliveryTested"));
        assertEquals(8L, stats.get("totalRoles"));
        assertEquals(139L, stats.get("totalStories"));
        assertEquals(23L, stats.get("designCompletePercent"));
        assertEquals(20L, stats.get("deliveryIntegratedPercent"));
    }

    @Test
    void shouldOmitPercentagesWhenNoScreensExist() {
        when(screenRepository.count()).thenReturn(0L);
        when(screenRepository.countByDesignStatus("COMPLETE")).thenReturn(0L);
        when(screenRepository.countByDesignStatus("SPECIFIED")).thenReturn(0L);
        when(screenRepository.countByDesignStatus("NOT_STARTED")).thenReturn(0L);
        when(screenRepository.countByDeliveryStatus("INTEGRATED")).thenReturn(0L);
        when(screenRepository.countByDeliveryStatus("TESTED")).thenReturn(0L);
        when(businessRoleRepository.count()).thenReturn(6L);
        when(validationRoleRepository.count()).thenReturn(2L);
        when(userStoryRepository.count()).thenReturn(139L);

        Map<String, Object> stats = screenService.getStats();

        assertEquals(8L, stats.get("totalRoles"));
        assertFalse(stats.containsKey("designCompletePercent"));
        assertFalse(stats.containsKey("deliveryIntegratedPercent"));
        assertTrue(stats.containsKey("totalStories"));
    }
}
