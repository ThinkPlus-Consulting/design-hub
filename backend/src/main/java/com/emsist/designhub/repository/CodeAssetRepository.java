package com.emsist.designhub.repository;
import com.emsist.designhub.domain.CodeAsset;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.List;

public interface CodeAssetRepository extends Neo4jRepository<CodeAsset, String> {
    List<CodeAsset> findByFilePath(String filePath);
}
