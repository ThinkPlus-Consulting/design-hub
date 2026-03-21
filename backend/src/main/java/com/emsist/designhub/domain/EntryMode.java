package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryMode {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String entryModeId;

    private String channelId;
    private String mechanism;
}
