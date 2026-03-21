package com.emsist.designhub.service;

import com.emsist.designhub.domain.Screen;
import com.emsist.designhub.repository.ScreenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final Neo4jClient neo4jClient;

    public List<Screen> getAllScreens() {
        log.debug("Fetching all screens");
        return screenRepository.findAll();
    }

    public Optional<Screen> getScreen(String surfaceId) {
        log.debug("Fetching full graph for screen: {}", surfaceId);
        return screenRepository.findFullGraph(surfaceId);
    }

    public List<Screen> getFilteredScreens(String module, String designStatus) {
        log.debug("Fetching filtered screens - module: {}, designStatus: {}", module, designStatus);
        return screenRepository.findFiltered(module, designStatus);
    }

    @Transactional
    public Screen saveNotes(String surfaceId, String notes) {
        log.info("Saving notes for screen: {}", surfaceId);
        Screen screen = screenRepository.findById(surfaceId)
                .orElseThrow(() -> new RuntimeException("Screen not found: " + surfaceId));
        screen.setNotes(notes);
        return screenRepository.save(screen);
    }

    public Optional<String> getNotes(String surfaceId) {
        log.debug("Fetching notes for screen: {}", surfaceId);
        return screenRepository.findById(surfaceId).map(Screen::getNotes);
    }

    public Map<String, Object> getStats() {
        log.debug("Calculating screen stats");
        long total = countNodes("Screen");
        long designComplete = screenRepository.countByDesignStatus("COMPLETE");
        long designSpecified = screenRepository.countByDesignStatus("SPECIFIED");
        long designNotStarted = screenRepository.countByDesignStatus("NOT_STARTED");
        long deliveryIntegrated = screenRepository.countByDeliveryStatus("INTEGRATED");
        long deliveryTested = screenRepository.countByDeliveryStatus("TESTED");

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalScreens", total);
        stats.put("designComplete", designComplete);
        stats.put("designSpecified", designSpecified);
        stats.put("designNotStarted", designNotStarted);
        stats.put("deliveryIntegrated", deliveryIntegrated);
        stats.put("deliveryTested", deliveryTested);
        stats.put("totalRoles", countNodes("BusinessRole") + countNodes("ValidationRole"));
        stats.put("totalStories", countNodes("UserStory"));

        if (total > 0) {
            stats.put("designCompletePercent", Math.round((double) designComplete / total * 100));
            stats.put("deliveryIntegratedPercent", Math.round((double) deliveryIntegrated / total * 100));
        }

        return stats;
    }

    private long countNodes(String label) {
        return neo4jClient.query("MATCH (n:%s) RETURN count(n) AS total".formatted(label))
                .fetch()
                .one()
                .map(record -> ((Number) record.get("total")).longValue())
                .orElse(0L);
    }
}
