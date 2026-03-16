package com.emsist.designhub.service;

import com.emsist.designhub.domain.Touchpoint;
import com.emsist.designhub.repository.TouchpointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TouchpointService {

    private final TouchpointRepository touchpointRepository;

    public List<Touchpoint> getAll() {
        log.debug("Fetching all touchpoints");
        return touchpointRepository.findAll();
    }
}
