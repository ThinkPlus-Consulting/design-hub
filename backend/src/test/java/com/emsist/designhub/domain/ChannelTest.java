package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChannelTest {

    @Test
    void shouldBuildChannelWithRequiredFields() {
        Channel channel = Channel.builder()
                .channelCode("CH-WEB-DSK")
                .displayName("Web Desktop")
                .channelType("WEB")
                .build();

        assertEquals("CH-WEB-DSK", channel.getChannelCode());
        assertEquals("Web Desktop", channel.getDisplayName());
        assertEquals("WEB", channel.getChannelType());
    }

    @Test
    void shouldFollowIdPattern() {
        Channel channel = Channel.builder()
                .channelCode("CH-API")
                .displayName("REST API")
                .channelType("API")
                .build();

        assertTrue(channel.getChannelCode().startsWith("CH-"));
    }

    @Test
    void shouldSupportAllPublishedChannelCodes() {
        String[] codes = {"CH-WEB-DSK", "CH-WEB-TAB", "CH-WEB-MOB", "CH-API", "CH-WEBHOOK",
                          "CH-AI-CHAT", "CH-AI-BG", "CH-EMAIL", "CH-INAPP"};
        assertEquals(9, codes.length);
        for (String code : codes) {
            Channel ch = Channel.builder()
                    .channelCode(code)
                    .displayName(code + " channel")
                    .channelType("WEB")
                    .build();
            assertTrue(ch.getChannelCode().startsWith("CH-"));
        }
    }
}
