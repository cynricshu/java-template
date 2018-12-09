// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model;

import java.util.Map;

import com.cynricshu.model.app.AppCategory;
import com.cynricshu.model.app.AppTemplate;
import com.cynricshu.model.k8s.K8sVersionUpdater;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Constant
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
public class Constant {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATETIME_OPENSTACK_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.FFF'Z'";
    // FFF microsecond, SSS millisecond
    public static final String DATETIME_LOCAL_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final Map<String, AppTemplate> TEMPLATE_MAP;

    public static final int DEFAULT_PAGE_SIZE = 15;

    static {
        AppTemplate asrengine = AppTemplate.builder().category(AppCategory.ASR)
                .name("asrengine").displayName("ASR引擎")
                .mainVersion("1.0")
                .mainVersionLocator(new K8sVersionUpdater().name("asrengine-deployment").containerName("asrengine"))
                .configFilePath(ImmutableList.of("env/manifests/application/asrengine.yaml",
                        "env/manifests/application/asrfrontend.yaml",
                        "env/manifests/application/asrmrcp.yaml"))
                .build();

        TEMPLATE_MAP = ImmutableMap.<String, AppTemplate>builder()
                .put(asrengine.getName(), asrengine)
                .build();
    }
}
