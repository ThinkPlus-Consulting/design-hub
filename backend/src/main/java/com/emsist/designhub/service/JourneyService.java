package com.emsist.designhub.service;

import com.emsist.designhub.domain.Journey;
import com.emsist.designhub.repository.JourneyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JourneyService {

    private final JourneyRepository journeyRepository;

    public List<Journey> getAll() {
        log.debug("Fetching all journeys");
        return journeyRepository.findAll();
    }
}
