// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.k8s;

import java.util.List;

import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1DaemonSet;
import io.kubernetes.client.models.V1Deployment;
import lombok.Data;

/**
 * VersionLocator
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
public class K8sVersionUpdater {
    private String kind = "Deployment";
    private String name;
    private String containerName;

    public K8sVersionUpdater kind(String kind) {
        this.kind = kind;
        return this;
    }

    public K8sVersionUpdater name(String name) {
        this.name = name;
        return this;
    }

    public K8sVersionUpdater containerName(String containerName) {
        this.containerName = containerName;
        return this;
    }

    public void updateVersion(String version, List<V1Deployment> deployments, List<V1DaemonSet> daemonSets) {
        List<V1Container> targets = this.findTargetContainers(deployments, daemonSets);

        if (targets != null) {
            // 替换container中的image
            targets.stream().filter(it -> it.getName().equals(containerName)).findFirst().ifPresent(it -> {
                String image = it.getImage();
                it.setImage(image.substring(0, image.lastIndexOf(":")) + ":" + version);
            });
        }
    }

    // 根据kind和name，找到对应的containers配置
    private List<V1Container> findTargetContainers(List<V1Deployment> deployments, List<V1DaemonSet> daemonSets) {
        for (V1Deployment it : deployments) {
            if (it.getKind().equals(kind) && it.getMetadata().getName().equals(name)) {
                return it.getSpec().getTemplate().getSpec().getContainers();
            }
        }

        for (V1DaemonSet it : daemonSets) {
            if (it.getKind().equals(kind) && it.getMetadata().getName().equals(name)) {
                return it.getSpec().getTemplate().getSpec().getContainers();
            }
        }

        return null;
    }

}
