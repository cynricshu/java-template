// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.k8s;

import io.kubernetes.client.models.V1ObjectMeta;
import lombok.Data;

/**
 * K8sObjectTracker
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
public class K8sObjectTracker {
    private String uid;
    private String kind;
    private String name;

    public static K8sObjectTracker fromObjectMeta(V1ObjectMeta meta) {
        K8sObjectTracker ret = new K8sObjectTracker();
        ret.setUid(meta.getUid());
        ret.setName(meta.getName());

        return ret;
    }
}
