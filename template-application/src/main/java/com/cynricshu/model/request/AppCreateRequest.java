// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.request;

import static com.cynricshu.model.k8s.K8sConstant.K8S_DEFAULT_COMMANDS;
import static com.cynricshu.model.k8s.K8sConstant.K8S_DEFAULT_NAMESPACE;
import static com.cynricshu.model.k8s.K8sConstant.K8S_DEFAULT_REPLICAS;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * AppCreateRequest
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
public class AppCreateRequest {
    private String namespace = K8S_DEFAULT_NAMESPACE;
    @NotBlank
    private String name;
    @NotBlank
    private String templateName;
    @NotBlank
    private String version;
    private int port;
    private Map<String, String> labels;
    private int replicas = K8S_DEFAULT_REPLICAS;
    private List<String> command = K8S_DEFAULT_COMMANDS;
    private String imageName;
    private String imageVersion;
}
