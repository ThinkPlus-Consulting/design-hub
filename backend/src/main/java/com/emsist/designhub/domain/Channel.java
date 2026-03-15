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
public class Channel {

    @Id
    private String channelCode;   // Pattern: CH-{code}

    private String displayName;
    private String channelType;   // WEB, MOBILE, TABLET, CHATBOT, KIOSK, API, VOICE
}
