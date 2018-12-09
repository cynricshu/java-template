// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.app;

import java.util.List;

import com.cynricshu.model.k8s.K8sVersionUpdater;

import io.kubernetes.client.models.V1DaemonSet;
import io.kubernetes.client.models.V1Deployment;
import lombok.Builder;
import lombok.Data;

/**
 * AppTemplate
 *
 * @author Shu Lingjie(shulingjie@baidu.com)s
 */
@Data
@Builder
public class AppTemplate {
    private AppCategory category;
    private String name;
    private String displayName;
    private String mainVersion;
    private K8sVersionUpdater mainVersionLocator;
    private List<String> configFilePath;

    public void updateVersion(List<V1Deployment> deployments, List<V1DaemonSet> daemonSets) {
        this.getMainVersionLocator().updateVersion(mainVersion, deployments, daemonSets);
    }

    public void updateName(List<Object> k8sObjects) {
        for (Object k8sObject : k8sObjects) {

        }
    }
}
