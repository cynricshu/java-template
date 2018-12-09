// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.request;

import static com.cynricshu.model.k8s.K8sConstant.K8S_DEFAULT_GRACE_PERIOD;
import static com.cynricshu.model.k8s.K8sConstant.K8S_DEFAULT_NAMESPACE;
import static com.cynricshu.model.k8s.K8sConstant.K8S_DEFAULT_PROPAGATION_POLICY;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * AppDeleteRequest
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
public class AppDeleteRequest {
    private String namespace = K8S_DEFAULT_NAMESPACE;
    @NotBlank
    private String name;
    private int gracePeriodSeconds = K8S_DEFAULT_GRACE_PERIOD;
    private String propagationPolicy = K8S_DEFAULT_PROPAGATION_POLICY;
}
