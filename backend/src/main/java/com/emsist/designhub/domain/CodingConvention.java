package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingConvention {
    @Id
    private String conventionCode;      // Pattern: CONV-{category}-{seq}

    private String name;
    private String category;            // NAMING, STRUCTURE, DEPENDENCY_INJECTION, ERROR_HANDLING, TESTING, LOGGING, SECURITY, API_DESIGN, DATABASE, DOCUMENTATION
    private String enforcement;         // MANDATORY, RECOMMENDED, ADVISORY
    private String scope;               // GLOBAL, BACKEND, FRONTEND, SERVICE, COMPONENT
    private String docRef;              // Relative path to convention Markdown file
    private String summary;             // Optional — one-line summary for quick agent reference
    private String activeStatus;        // ACTIVE, DEPRECATED
}
