package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("TranslationKey")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationKeyEntry {

    @Id
    private String key;

    private String defaultText;
    private String context;
}
