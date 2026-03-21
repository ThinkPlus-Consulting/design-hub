package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;

public interface ExternalSyncPollingClient {

    String fetchPayload(String sourceSystem, ExternalSyncProperties.SourceProperties sourceProperties);
}
