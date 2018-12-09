// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.k8s;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * K8sConstant
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
public class K8sConstant {
    public static final String K8S_DEFAULT_NAMESPACE = "default";
    public static final int K8S_DEFAULT_REPLICAS = 3;
    public static final int K8S_DEFAULT_GRACE_PERIOD = 30;
    public static final String K8S_DEFAULT_PROPAGATION_POLICY = "Foreground";
    public static final List<String> K8S_DEFAULT_COMMANDS = ImmutableList.of("/bin/sh");
}
