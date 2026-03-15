package com.emsist.designhub.repository;

import com.emsist.designhub.domain.ConfirmationDialog;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationDialogRepository extends Neo4jRepository<ConfirmationDialog, String> {
}
