package com.emsist.designhub.service;

import com.emsist.designhub.dto.UserStoryResponse;
import com.emsist.designhub.repository.UserStoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;

    public List<UserStoryResponse> getAll() {
        log.debug("Fetching user story graph summaries");
        return userStoryRepository.findAllSummaries().stream()
                .map(UserStoryResponse::from)
                .toList();
    }
}
