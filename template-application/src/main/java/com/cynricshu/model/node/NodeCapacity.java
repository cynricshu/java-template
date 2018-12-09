// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.node;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.kubernetes.client.models.V1NodeStatus;
import lombok.Data;

/**
 * NodeResource
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
public class NodeCapacity {
    private String cpu;
    private String memory;

    public static NodeCapacity fromNodeStatus(V1NodeStatus status) {
        NodeCapacity capacity = new NodeCapacity();
        capacity.setCpu(status.getCapacity().get("cpu").toSuffixedString());
        capacity.setMemory(status.getCapacity().get("memory").getNumber()
                .divide(BigDecimal.valueOf(1024 * 1024), 2, RoundingMode.HALF_DOWN).toPlainString() + "Mi");

        return capacity;
    }
}
