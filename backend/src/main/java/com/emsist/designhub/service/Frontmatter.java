package com.emsist.designhub.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Frontmatter {
    private String id;
    private String type;
    private String status;
    private int version;
    private List<String> delivers;
    private List<String> verifiedBy;
    private List<String> realizes;
    private String executionMode;
}
