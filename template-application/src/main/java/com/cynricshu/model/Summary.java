// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model;

import java.time.Instant;
import java.util.Map;

import io.kubernetes.client.models.V1ObjectMeta;
import lombok.Data;

/**
 * Summary
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
public class Summary {
    private String uid;
    private String name;
    private String namespace;
    private String resourceVersion;
    private String selfLink;
    private Instant creationTimestamp;
    private Map<String, String> annotations;
    private Map<String, String> labels;

    public static <T extends Summary> T fromObjectMeta(T object, V1ObjectMeta meta) {
        object.setUid(meta.getUid());
        object.setName(meta.getName());
        object.setNamespace(meta.getNamespace());
        object.setCreationTimestamp(Instant.ofEpochMilli(meta.getCreationTimestamp().getMillis()));
        object.setLabels(meta.getLabels());

        return object;
    }
}
