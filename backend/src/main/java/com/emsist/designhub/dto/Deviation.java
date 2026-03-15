package com.emsist.designhub.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deviation {
    private String type;         // EXTRA_FILE, MISSING_FILE, UNEXPECTED_CHANGE, SCOPE_EXCEEDED
    private String description;
    private String severity;     // INFO, WARNING, BLOCKING
}
