// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.node;

import com.cynricshu.model.Summary;

import io.kubernetes.client.models.V1NodeStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * NodeSummary
 *
 * @author Xiong Chao(xiongchao@baidu.com)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeSummary extends Summary {
    private NodeStatus status = NodeStatus.NOT_READY;
    private String ipAddr;
    private NodeCapacity capacity;

    public NodeSummary fillIp(V1NodeStatus v1NodeStatus) {
        v1NodeStatus.getAddresses().stream().filter(address -> {
            return address.getType().equalsIgnoreCase("internalip");
        }).findFirst().ifPresent(address -> {
            this.setIpAddr(address.getAddress());
        });

        return this;
    }

    public NodeSummary fillStatus(V1NodeStatus v1NodeStatus) {
        v1NodeStatus.getConditions().stream().filter(cond -> {
            return cond.getType().equalsIgnoreCase("ready");
        }).findFirst().ifPresent(cond -> {
            this.setStatus(NodeStatus.READY);
        });

        return this;
    }
}
