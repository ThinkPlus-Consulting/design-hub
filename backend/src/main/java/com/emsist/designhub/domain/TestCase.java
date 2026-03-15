package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    @Id
    private String testCaseId;
    private String title;
    private String description;
    private String testType;
    private String preconditions;
    private String expectedResult;
    private Status status;

    // Agent-ready enrichment (7 new attributes)
    private String testFilePath;
    private String testClassName;
    private String testMethodName;
    private String testFramework;
    private String suiteName;
    private List<String> tags;
    private String testCommand;
    private List<String> expectedAssertions;

    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.OUTGOING)
    private CodeAsset locatedIn;
}
