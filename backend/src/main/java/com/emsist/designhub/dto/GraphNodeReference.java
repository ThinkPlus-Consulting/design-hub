package com.emsist.designhub.dto;

public record GraphNodeReference(
        String id,
        String nodeType,
        String displayName,
        String status
) {
}
