// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.app;

import java.time.Instant;
import java.util.List;

import com.cynricshu.model.k8s.K8sObjectTracker;

import lombok.Data;

/**
 * App
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
public class App {
    private long id;
    private String name;
    private Instant createTime;
    private List<K8sObjectTracker> k8sObjectTrackers;
}
