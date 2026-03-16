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
public class BusinessDomain {
    @Id
    private String domainCode;
    private String name;
    private String description;
    private String activeStatus;

    @Relationship(type = "HAS_CAPABILITY", direction = Relationship.Direction.OUTGOING)
    private List<BusinessCapability> capabilities;
}
