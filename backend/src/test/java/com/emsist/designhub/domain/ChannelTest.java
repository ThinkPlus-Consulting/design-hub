package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChannelTest {

    @Test
    void shouldBuildChannelWithRequiredFields() {
        Channel channel = Channel.builder()
                .channelCode("CH-WEB")
                .displayName("Web Browser")
                .channelType("WEB")
                .build();

        assertEquals("CH-WEB", channel.getChannelCode());
        assertEquals("Web Browser", channel.getDisplayName());
        assertEquals("WEB", channel.getChannelType());
    }

    @Test
    void shouldFollowIdPattern() {
        Channel channel = Channel.builder()
                .channelCode("CH-MOBILE")
                .displayName("Mobile App")
                .channelType("MOBILE")
                .build();

        assertTrue(channel.getChannelCode().startsWith("CH-"));
    }

    @Test
    void shouldSupportAllChannelTypes() {
        for (String type : new String[]{"WEB", "MOBILE", "TABLET", "CHATBOT", "KIOSK", "API", "VOICE"}) {
            Channel ch = Channel.builder()
                    .channelCode("CH-" + type)
                    .displayName(type + " channel")
                    .channelType(type)
                    .build();
            assertEquals(type, ch.getChannelType());
        }
    }
}
