package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRole {

    @Id
    private String roleKey;       // SUPER_ADMIN, ADMIN, ARCHITECT, AGENT_DESIGNER, USER, VIEWER

    private String displayName;
    private String roleGroup;     // platform, tenant, design, operational
    private String scope;
    private Integer sortOrder;
    private Status status;
    private List<String> sourceRefs;
}
