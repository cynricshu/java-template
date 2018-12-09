// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.request;

import java.time.Instant;

import com.cynricshu.model.k8s.K8sConstant;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AppListRequest
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppListRequest extends PageRequest {
    private String namespace = K8sConstant.K8S_DEFAULT_NAMESPACE;
    private Instant createTime;
}
