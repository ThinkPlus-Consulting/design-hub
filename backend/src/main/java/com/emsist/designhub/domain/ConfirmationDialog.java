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
public class ConfirmationDialog {

    @Id
    private String dialogId;

    private String triggerAction;
    private String confirmLabel;
    private String cancelLabel;
    private String consequenceText;
}
