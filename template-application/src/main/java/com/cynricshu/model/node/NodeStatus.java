// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.node;

import lombok.Getter;

/**
 * NodeStatus
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
public enum NodeStatus {
    READY("Ready"),
    NOT_READY("Not Ready");

    @Getter
    private String text;

    NodeStatus(String text) {
        this.text = text;
    }
}
