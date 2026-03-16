package com.emsist.designhub.service;

import com.emsist.designhub.domain.Interaction;
import com.emsist.designhub.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InteractionService {

    private final InteractionRepository interactionRepository;

    public List<Interaction> getAll() {
        log.debug("Fetching all interactions");
        return interactionRepository.findAll();
    }

    public List<Interaction> getBySurfaceId(String surfaceId) {
        log.debug("Fetching interactions for screen: {}", surfaceId);
        return interactionRepository.findBySurfaceId(surfaceId);
    }
}
