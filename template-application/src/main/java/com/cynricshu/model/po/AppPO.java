// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.po;

import java.time.Instant;

import lombok.Data;

/**
 * AppPO
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
public class AppPO {
    private long id;
    private String name;
    private Instant createTime;

    private String k8sObjectTrackers;
}
